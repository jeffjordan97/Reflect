package com.reflect.service;

import com.reflect.config.ReflectProperties;
import com.reflect.domain.CheckIn;
import com.reflect.domain.Insight;
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

            Voice:
            - Calm and warm, never prescriptive
            - Acknowledge uncertainty ("this suggests..." not "you are...")
            - Gentle observation, not advice
            - Never use urgency or exclamation marks
            - Refer to the user in second person

            Keep it to 2-3 sentences maximum. Focus on one concrete observation that pairs two
            fields (e.g., energy + friction, or signal moment + wins) rather than summarizing
            everything. Avoid bullet points. Write as flowing prose.
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

            String userMessage = buildUserMessage(checkIn);
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

    private String buildUserMessage(CheckIn checkIn) {
        StringBuilder sb = new StringBuilder("Here is this week's check-in:\n\n");
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
