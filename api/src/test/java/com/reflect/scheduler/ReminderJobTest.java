package com.reflect.scheduler;

import com.reflect.config.ReflectProperties;
import com.reflect.domain.CheckIn;
import com.reflect.domain.User;
import com.reflect.repository.CheckInRepository;
import com.reflect.repository.UserRepository;
import com.reflect.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderJobTest {

    @Mock private UserRepository userRepository;
    @Mock private CheckInRepository checkInRepository;
    @Mock private EmailService emailService;

    private ReminderJob reminderJob;

    @BeforeEach
    void setUp() {
        ReflectProperties props = new ReflectProperties(
                null, null, null, null, null, null, null, null, null, null, "https://reflect.app"
        );
        reminderJob = new ReminderJob(userRepository, checkInRepository, emailService, props);
    }

    @Test
    void sendSundayReminders_sendsEmailToEligibleUserWithoutCheckIn() {
        User user = createVerifiedUser("alice@example.com", "Alice");
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        when(userRepository.findEligibleForReminder()).thenReturn(List.of(user));
        when(checkInRepository.findByUserIdAndWeekStart(user.getId(), sunday))
                .thenReturn(Optional.empty());

        reminderJob.sendSundayReminders();

        verify(emailService).sendReminderEmail("alice@example.com", "Alice", "https://reflect.app");
    }

    @Test
    void sendSundayReminders_skipsUserWhoAlreadyCompletedCheckIn() {
        User user = createVerifiedUser("bob@example.com", "Bob");
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        CheckIn completedCheckIn = new CheckIn(user, sunday);
        completedCheckIn.setCompleted(true);

        when(userRepository.findEligibleForReminder()).thenReturn(List.of(user));
        when(checkInRepository.findByUserIdAndWeekStart(user.getId(), sunday))
                .thenReturn(Optional.of(completedCheckIn));

        reminderJob.sendSundayReminders();

        verify(emailService, never()).sendReminderEmail(any(), any(), any());
    }

    @Test
    void sendSundayReminders_sendsToUserWithIncompleteCheckIn() {
        User user = createVerifiedUser("carol@example.com", "Carol");
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        CheckIn incompleteCheckIn = new CheckIn(user, sunday);
        // completed defaults to false

        when(userRepository.findEligibleForReminder()).thenReturn(List.of(user));
        when(checkInRepository.findByUserIdAndWeekStart(user.getId(), sunday))
                .thenReturn(Optional.of(incompleteCheckIn));

        reminderJob.sendSundayReminders();

        verify(emailService).sendReminderEmail("carol@example.com", "Carol", "https://reflect.app");
    }

    @Test
    void sendSundayReminders_handlesEmailFailureGracefully() {
        User user1 = createVerifiedUser("fail@example.com", "FailUser");
        User user2 = createVerifiedUser("succeed@example.com", "SucceedUser");
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        when(userRepository.findEligibleForReminder()).thenReturn(List.of(user1, user2));
        when(checkInRepository.findByUserIdAndWeekStart(any(), eq(sunday)))
                .thenReturn(Optional.empty());
        doThrow(new RuntimeException("SMTP error"))
                .when(emailService).sendReminderEmail("fail@example.com", "FailUser", "https://reflect.app");

        reminderJob.sendSundayReminders();

        // Second user still receives email despite first failure
        verify(emailService).sendReminderEmail("succeed@example.com", "SucceedUser", "https://reflect.app");
    }

    @Test
    void sendSundayReminders_sendsToFreeTierUser() {
        User freeUser = createVerifiedUser("free@example.com", "FreeUser");
        // subscriptionStatus defaults to "FREE" — reminders are not a Pro feature
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        when(userRepository.findEligibleForReminder()).thenReturn(List.of(freeUser));
        when(checkInRepository.findByUserIdAndWeekStart(freeUser.getId(), sunday))
                .thenReturn(Optional.empty());

        reminderJob.sendSundayReminders();

        verify(emailService).sendReminderEmail("free@example.com", "FreeUser", "https://reflect.app");
    }

    @Test
    void sendSundayReminders_sendsNothingWhenNoEligibleUsers() {
        when(userRepository.findEligibleForReminder()).thenReturn(List.of());

        reminderJob.sendSundayReminders();

        verify(emailService, never()).sendReminderEmail(any(), any(), any());
    }

    private User createVerifiedUser(String email, String displayName) {
        User user = new User(email, "hash", displayName);
        user.setEmailVerified(true);
        user.setRemindersEnabled(true);
        return user;
    }
}
