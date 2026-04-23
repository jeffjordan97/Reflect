package com.reflect.service;

import com.reflect.config.ReflectProperties;
import com.reflect.domain.CheckIn;
import com.reflect.domain.Insight;
import com.reflect.exception.ApiException;
import com.reflect.repository.CheckInRepository;
import com.reflect.repository.InsightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InsightService {

    private static final Logger log = LoggerFactory.getLogger(InsightService.class);

    private static final String SYSTEM_PROMPT = """
            You are a reflective observer for Reflect, a guided weekly review app.
            Your role is to offer a brief, 2-3 sentence reflection on a user's weekly check-in
            that picks up on something specific they wrote.

            ## Voice
            - Calm and warm, never prescriptive or performative
            - Acknowledge uncertainty: "this suggests..." not "you are..."
            - Gentle observation, not advice. Never say "you should", "try to", or "consider"
            - Never use urgency, exclamation marks, or superlatives
            - Refer to the user in second person
            - Never simulate emotions: no "I feel", "I'm sorry to hear", "that must be hard",
              or "that's wonderful". Demonstrate understanding through accurate reflection,
              not emotional performance

            ## What to do
            - Focus on one concrete observation that pairs two fields (e.g., energy + friction,
              or signal moment + intentions) rather than summarising everything
            - Amplify the user's own language. If they used words like "need", "want", or "will",
              reflect that back — their own commitment language matters more than your analysis
            - When a previous week's intentions are provided, note any connection between what
              they intended and what they reported this week. Do not judge; just surface the link
            - Look for exceptions: if a recurring pattern broke this week, notice what was different
            - If the user's intentions are vague ("be more productive"), notice that specificity
              might help, without prescribing what specifically to do

            ## After a hard week (low energy, heavy friction)
            - Never imply they should have done better
            - Normalise difficulty: "Weeks like this are part of the picture"
            - Acknowledge what they did notice or name — that act of reflection has value
            - Do not promise next week will be better

            ## Format
            - 2-3 sentences maximum, flowing prose, no bullet points
            - One observation, not a summary
            """;

    private static final int MAX_OUTPUT_TOKENS = 250;

    private final InsightRepository insightRepository;
    private final CheckInRepository checkInRepository;
    private final AnthropicClient anthropicClient;
    private final String model;

    public InsightService(
            InsightRepository insightRepository,
            CheckInRepository checkInRepository,
            AnthropicClient anthropicClient,
            ReflectProperties properties
    ) {
        this.insightRepository = insightRepository;
        this.checkInRepository = checkInRepository;
        this.anthropicClient = anthropicClient;
        this.model = properties.anthropic().modelHaiku();
    }

    /**
     * Generate an insight for a completed check-in.
     *
     * Safe to call multiple times — dedupes on check_in_id. Fails silently if
     * the Anthropic call or anything else breaks, because this runs out-of-band
     * and must never break the user-visible check-in flow.
     */
    @Async
    @Transactional
    public void generateFor(UUID checkInId) {
        try {
            if (insightRepository.existsByCheckInId(checkInId)) {
                log.debug("Insight already exists for check-in {}, skipping", checkInId);
                return;
            }

            CheckIn checkIn = checkInRepository.findById(checkInId).orElse(null);
            if (checkIn == null) {
                log.warn("Check-in {} not found, skipping insight generation", checkInId);
                return;
            }
            if (!checkIn.isCompleted()) {
                log.debug("Check-in {} not completed, skipping insight generation", checkInId);
                return;
            }

            // Look up previous week's intentions to close the feedback loop
            Optional<CheckIn> previousWeek = checkInRepository.findByUserIdAndWeekStart(
                    checkIn.getUser().getId(),
                    checkIn.getWeekStart().minusWeeks(1)
            );
            String previousIntentions = previousWeek
                    .filter(CheckIn::isCompleted)
                    .map(CheckIn::getIntentions)
                    .filter(this::hasText)
                    .orElse(null);

            String userMessage = buildUserMessage(checkIn, previousIntentions);
            AnthropicClient.MessageResult result = anthropicClient.sendMessage(
                    model,
                    MAX_OUTPUT_TOKENS,
                    SYSTEM_PROMPT,
                    List.of(new AnthropicClient.Message("user", userMessage))
            );

            Insight insight = new Insight(
                    checkIn.getUser(),
                    checkIn,
                    result.text().trim(),
                    result.model(),
                    result.usage().inputTokens(),
                    result.usage().outputTokens()
            );
            insightRepository.save(insight);
            log.info("Generated insight for check-in {} ({} input tokens, {} output tokens)",
                    checkInId, result.usage().inputTokens(), result.usage().outputTokens());
        } catch (Exception e) {
            log.error("Failed to generate insight for check-in {}: {}", checkInId, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Optional<Insight> getByCheckIn(UUID checkInId, UUID userId) {
        return insightRepository.findByCheckInIdAndUserId(checkInId, userId);
    }

    /**
     * Manually trigger insight generation for a check-in. Verifies the caller
     * owns the check-in, then fires the async generation.
     * Used for backfilling insights on pre-existing check-ins.
     */
    public void requestGeneration(UUID checkInId, UUID userId) {
        CheckIn checkIn = checkInRepository.findByIdAndUserId(checkInId, userId)
                .orElseThrow(() -> ApiException.notFound("Check-in not found"));
        if (!checkIn.isCompleted()) {
            throw ApiException.badRequest("Check-in is not completed yet");
        }
        generateFor(checkInId);
    }

    private String buildUserMessage(CheckIn checkIn, String previousIntentions) {
        StringBuilder sb = new StringBuilder();

        // Include last week's intentions for feedback loop (Locke & Latham)
        if (previousIntentions != null) {
            sb.append("**Last week's intentions:** ").append(previousIntentions).append("\n\n");
            sb.append("---\n\n");
        }

        sb.append("Here is this week's check-in:\n\n");
        if (hasText(checkIn.getWins())) {
            sb.append("**Wins:** ").append(checkIn.getWins()).append("\n\n");
        }
        if (hasText(checkIn.getFriction())) {
            sb.append("**Friction:** ").append(checkIn.getFriction()).append("\n\n");
        }
        if (checkIn.getEnergyRating() != null) {
            sb.append("**Energy:** ").append(checkIn.getEnergyRating()).append("/10\n\n");
        }
        if (hasText(checkIn.getSignalMoment())) {
            sb.append("**Signal moment:** ").append(checkIn.getSignalMoment()).append("\n\n");
        }
        if (hasText(checkIn.getIntentions())) {
            sb.append("**Intentions for next week:** ").append(checkIn.getIntentions()).append("\n\n");
        }
        sb.append("Offer a brief reflection.");
        return sb.toString();
    }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
