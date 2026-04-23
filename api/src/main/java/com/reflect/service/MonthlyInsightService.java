package com.reflect.service;

import com.reflect.config.ReflectProperties;
import com.reflect.domain.CheckIn;
import com.reflect.domain.MonthlyInsight;
import com.reflect.domain.User;
import com.reflect.repository.CheckInRepository;
import com.reflect.repository.MonthlyInsightRepository;
import com.reflect.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MonthlyInsightService {

    private static final Logger log = LoggerFactory.getLogger(MonthlyInsightService.class);

    private static final int CHECK_INS_PER_MONTHLY = 4;

    private static final String MONTHLY_SYSTEM_PROMPT = """
            You are a reflective observer for Reflect, a guided weekly review app.
            You are writing a monthly synthesis — a look across the user's last 4 weekly
            check-ins to surface patterns, trends, and connections that a single-week
            reflection cannot see.

            ## Voice
            - Same calm, warm, non-prescriptive tone as weekly reflections
            - Never simulate emotions or give advice
            - Acknowledge uncertainty: "a pattern seems to be..." not "you clearly..."
            - Amplify the user's own language and themes

            ## What to cover (4-6 sentences total)
            - Energy arc: how did their energy move across the 4 weeks? Any trend?
            - Recurring friction: did the same themes appear multiple weeks?
            - Intention follow-through: did intentions from earlier weeks show up as wins later?
            - One forward-looking observation: based on the trajectory, what thread
              seems worth paying attention to next?

            ## Format
            - 4-6 sentences, flowing prose, no bullet points or headers
            - Written as a single cohesive paragraph, not four separate observations
            - Do not reference specific week numbers ("in week 2..."). Instead use
              phrasing like "earlier in the month" or "by the end of the month"
            """;

    private static final DateTimeFormatter WEEK_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    private final MonthlyInsightRepository monthlyInsightRepository;
    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final AnthropicClient anthropicClient;
    private final String model;
    private final int maxOutputTokens;

    public MonthlyInsightService(
            MonthlyInsightRepository monthlyInsightRepository,
            CheckInRepository checkInRepository,
            UserRepository userRepository,
            AnthropicClient anthropicClient,
            ReflectProperties properties
    ) {
        this.monthlyInsightRepository = monthlyInsightRepository;
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
        this.anthropicClient = anthropicClient;
        this.model = properties.anthropic().modelHaiku();
        this.maxOutputTokens = properties.anthropic().maxTokensMonthlyInsight();
    }

    /**
     * Check whether a monthly insight is due for the user and generate one if so.
     * Called after each weekly insight generation. Fails silently to avoid
     * disrupting the check-in flow.
     */
    public void generateIfDue(UUID userId) {
        try {
            long completedCount = checkInRepository.countCompletedByUserId(userId);
            long monthlyInsightCount = monthlyInsightRepository.countByUserId(userId);

            if (completedCount / CHECK_INS_PER_MONTHLY > monthlyInsightCount) {
                log.info("Monthly insight due for user {} (completed={}, existing={})",
                        userId, completedCount, monthlyInsightCount);
                generateMonthlyInsight(userId);
            }
        } catch (Exception e) {
            log.error("Failed to check/generate monthly insight for user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Generate a monthly synthesis insight from the user's last 4 completed check-ins.
     */
    @Async
    @Transactional
    public void generateMonthlyInsight(UUID userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("User {} not found, skipping monthly insight generation", userId);
                return;
            }

            List<CheckIn> recentCheckIns = checkInRepository.findCompletedByUserIdDesc(
                    userId, PageRequest.of(0, CHECK_INS_PER_MONTHLY));

            if (recentCheckIns.size() < CHECK_INS_PER_MONTHLY) {
                log.debug("User {} has fewer than {} completed check-ins, skipping monthly insight",
                        userId, CHECK_INS_PER_MONTHLY);
                return;
            }

            // Reverse to oldest-first for the prompt
            List<CheckIn> oldestFirst = new ArrayList<>(recentCheckIns);
            java.util.Collections.reverse(oldestFirst);

            String userMessage = buildUserMessage(oldestFirst);
            AnthropicClient.MessageResult result = anthropicClient.sendMessage(
                    model,
                    maxOutputTokens,
                    MONTHLY_SYSTEM_PROMPT,
                    List.of(new AnthropicClient.Message("user", userMessage))
            );

            MonthlyInsight monthlyInsight = new MonthlyInsight(
                    user,
                    result.text().trim(),
                    result.model(),
                    result.usage().inputTokens(),
                    result.usage().outputTokens(),
                    oldestFirst.getFirst().getWeekStart(),
                    oldestFirst.getLast().getWeekStart(),
                    CHECK_INS_PER_MONTHLY
            );
            monthlyInsightRepository.save(monthlyInsight);

            log.info("Generated monthly insight for user {} ({} input tokens, {} output tokens)",
                    userId, result.usage().inputTokens(), result.usage().outputTokens());
        } catch (Exception e) {
            log.error("Failed to generate monthly insight for user {}: {}", userId, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Optional<MonthlyInsight> getLatest(UUID userId) {
        return monthlyInsightRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<MonthlyInsight> getAll(UUID userId) {
        return monthlyInsightRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    private String buildUserMessage(List<CheckIn> checkIns) {
        StringBuilder sb = new StringBuilder();
        sb.append("Here are the last 4 weekly check-ins, oldest to newest:\n\n");

        for (CheckIn checkIn : checkIns) {
            sb.append("---\n");
            sb.append("Week of ").append(checkIn.getWeekStart().format(WEEK_FORMATTER)).append(":\n");

            if (hasText(checkIn.getWins())) {
                sb.append("**Wins:** ").append(checkIn.getWins()).append("\n");
            }
            if (hasText(checkIn.getFriction())) {
                sb.append("**Friction:** ").append(checkIn.getFriction()).append("\n");
            }
            if (checkIn.getEnergyRating() != null) {
                sb.append("**Energy:** ").append(checkIn.getEnergyRating()).append("/10\n");
            }
            if (hasText(checkIn.getSignalMoment())) {
                sb.append("**Signal moment:** ").append(checkIn.getSignalMoment()).append("\n");
            }
            if (hasText(checkIn.getIntentions())) {
                sb.append("**Intentions:** ").append(checkIn.getIntentions()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("---\n\nOffer a monthly synthesis.");
        return sb.toString();
    }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
