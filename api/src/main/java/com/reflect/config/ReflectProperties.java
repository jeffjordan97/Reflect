package com.reflect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.DayOfWeek;

/**
 * Strongly-typed binding for all reflect.* properties in application.yml.
 *
 * Registered automatically via @ConfigurationPropertiesScan on ReflectApplication.
 * Injected via constructor injection in services and agents that need config values.
 *
 * Never inject @Value in service layer — always inject ReflectProperties.
 * This makes configuration explicit, testable, and refactor-safe.
 *
 * Usage:
 *   private final ReflectProperties props;
 *   props.getFreeTier().getMaxCheckIns()
 *   props.getAnthropic().getModelHaiku()
 */
@ConfigurationProperties(prefix = "reflect")
public record ReflectProperties(
        Jwt jwt,
        Anthropic anthropic,
        Stripe stripe,
        SendGrid sendgrid,
        FreeTier freeTier,
        Reminder reminder,
        Agent agent,
        Mcp mcp,
        Security security,
        Resend resend,
        String frontendUrl
) {

    public record Jwt(
            String privateKey,
            String publicKey,
            long accessTokenExpirySeconds,
            long refreshTokenExpirySeconds
    ) {}

    public record Anthropic(
            String apiKey,
            String baseUrl,
            String apiVersion,
            String modelHaiku,
            String modelSonnet,
            int timeoutSeconds,
            int maxTokensMonthlyInsight,
            int maxTokensQuarterlyInsight,
            int maxTokensNudge,
            int maxTokensInterpersonalClassify
    ) {}

    public record Stripe(
            String secretKey,
            String webhookSecret,
            String priceIdMonthly,
            String priceIdAnnual
    ) {}

    public record SendGrid(
            String apiKey,
            String fromEmail,
            String fromName,
            String templateWelcome,
            String templateSundayReminder,
            String templateMonthlyDigest,
            String templateNudge,
            String templateBillingAlert,
            String templatePasswordReset,
            String templateEmailVerify
    ) {}

    public record FreeTier(
            int maxCheckIns
    ) {}

    public record Reminder(
            String defaultTime,
            DayOfWeek defaultDay
    ) {}

    public record Agent(
            int minEntriesForPattern,
            int nudgeStrengthThreshold,
            int nudgeCooldownDays,
            int avoidanceWordCountThreshold
    ) {}

    public record Mcp(
            int rateLimitPerHour
    ) {}

    public record Security(
            int bcryptStrength,
            int maxLoginAttempts,
            int lockoutMinutes,
            int resetTokenTtlHours,
            int verifyTokenTtlHours,
            int apiKeyRotationDays
    ) {}

    public record Resend(
            String apiKey,
            String fromEmail
    ) {}
}
