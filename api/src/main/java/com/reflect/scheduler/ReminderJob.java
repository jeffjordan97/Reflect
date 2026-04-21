package com.reflect.scheduler;

import com.reflect.config.ReflectProperties;
import com.reflect.domain.User;
import com.reflect.repository.CheckInRepository;
import com.reflect.repository.UserRepository;
import com.reflect.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
public class ReminderJob {

    private static final Logger log = LoggerFactory.getLogger(ReminderJob.class);

    private final UserRepository userRepository;
    private final CheckInRepository checkInRepository;
    private final EmailService emailService;
    private final String frontendUrl;

    public ReminderJob(
            UserRepository userRepository,
            CheckInRepository checkInRepository,
            EmailService emailService,
            ReflectProperties properties
    ) {
        this.userRepository = userRepository;
        this.checkInRepository = checkInRepository;
        this.emailService = emailService;
        this.frontendUrl = properties.frontendUrl() != null
                ? properties.frontendUrl()
                : "http://localhost:3000";
    }

    /**
     * Runs every Sunday at 09:00 UTC.
     * Finds users who have reminders enabled, a verified email,
     * and no completed check-in for the current week, then sends
     * a gentle nudge email.
     */
    @Scheduled(cron = "0 0 9 * * SUN", zone = "UTC")
    public void sendSundayReminders() {
        log.info("Starting Sunday reminder job");
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        List<User> eligibleUsers = userRepository.findEligibleForReminder();
        int sent = 0;
        int skipped = 0;

        for (User user : eligibleUsers) {
            boolean hasCompleted = checkInRepository
                    .findByUserIdAndWeekStart(user.getId(), sunday)
                    .map(c -> c.isCompleted())
                    .orElse(false);

            if (hasCompleted) {
                skipped++;
                continue;
            }

            try {
                emailService.sendReminderEmail(user.getEmail(), user.getDisplayName(), frontendUrl);
                sent++;
            } catch (Exception e) {
                log.error("Failed to send reminder to {}: {}", user.getEmail(), e.getMessage());
            }
        }

        log.info("Sunday reminder job complete: {} sent, {} skipped (already completed)", sent, skipped);
    }
}
