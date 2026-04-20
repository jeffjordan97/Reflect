package com.reflect.service;

import com.reflect.config.ReflectProperties;
import com.reflect.controller.dto.CheckInRequest;
import com.reflect.domain.CheckIn;
import com.reflect.domain.User;
import com.reflect.exception.ApiException;
import com.reflect.repository.CheckInRepository;
import com.reflect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInServiceTest {

    @Mock private CheckInRepository checkInRepository;
    @Mock private UserRepository userRepository;
    @Mock private InsightService insightService;

    private CheckInService checkInService;
    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        ReflectProperties.FreeTier freeTier = new ReflectProperties.FreeTier(4);
        ReflectProperties props = new ReflectProperties(
                null, null, null, null, freeTier, null, null, null, null, null, null
        );
        checkInService = new CheckInService(checkInRepository, userRepository, insightService, props);
        userId = UUID.randomUUID();
        user = new User("test@example.com", "hash", "Test User");
    }

    @Test
    void create_createsNewCheckInForCurrentWeek() {
        var request = new CheckInRequest("Won a deal", null, null, null, null, null);
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(checkInRepository.countCompletedByUserId(userId)).thenReturn(0L);
        when(checkInRepository.findByUserIdAndWeekStart(userId, sunday)).thenReturn(Optional.empty());
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(inv -> inv.getArgument(0));

        CheckIn result = checkInService.create(userId, request);
        assertNotNull(result);
        assertEquals(sunday, result.getWeekStart());
        assertEquals("Won a deal", result.getWins());
        assertFalse(result.isCompleted());
    }

    @Test
    void create_throwsConflictIfCheckInAlreadyExists() {
        var request = new CheckInRequest("Wins", null, null, null, null, null);
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        CheckIn existing = new CheckIn(user, sunday);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(checkInRepository.countCompletedByUserId(userId)).thenReturn(0L);
        when(checkInRepository.findByUserIdAndWeekStart(userId, sunday)).thenReturn(Optional.of(existing));

        ApiException ex = assertThrows(ApiException.class, () -> checkInService.create(userId, request));
        assertEquals(409, ex.getStatus().value());
    }

    @Test
    void update_updatesExistingCheckIn() {
        UUID checkInId = UUID.randomUUID();
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        CheckIn checkIn = new CheckIn(user, sunday);
        checkIn.setWins("Old wins");
        var request = new CheckInRequest("New wins", "Some friction", (short) 7, null, null, null);
        when(checkInRepository.findByIdAndUserId(checkInId, userId)).thenReturn(Optional.of(checkIn));
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(inv -> inv.getArgument(0));

        CheckIn result = checkInService.update(checkInId, userId, request);
        assertEquals("New wins", result.getWins());
        assertEquals("Some friction", result.getFriction());
        assertEquals((short) 7, result.getEnergyRating());
    }

    @Test
    void update_throwsNotFoundForWrongUser() {
        UUID checkInId = UUID.randomUUID();
        var request = new CheckInRequest(null, null, null, null, null, null);
        when(checkInRepository.findByIdAndUserId(checkInId, userId)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> checkInService.update(checkInId, userId, request));
        assertEquals(404, ex.getStatus().value());
    }

    @Test
    void getCurrent_returnsCheckInForCurrentWeek() {
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        CheckIn checkIn = new CheckIn(user, sunday);
        when(checkInRepository.findByUserIdAndWeekStart(userId, sunday)).thenReturn(Optional.of(checkIn));

        Optional<CheckIn> result = checkInService.getCurrent(userId);
        assertTrue(result.isPresent());
    }

    @Test
    void list_returnsPaginatedResults() {
        CheckIn checkIn = new CheckIn(user, LocalDate.now());
        Page<CheckIn> page = new PageImpl<>(List.of(checkIn));
        when(checkInRepository.findByUserIdOrderByWeekStartDesc(eq(userId), any())).thenReturn(page);

        Page<CheckIn> result = checkInService.list(userId, PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void update_triggersInsightGenerationWhenCompletedTransitionsToTrue() {
        UUID checkInId = UUID.randomUUID();
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        CheckIn checkIn = new CheckIn(user, sunday);
        // starts as completed=false by default
        var request = new CheckInRequest("Wins", null, null, null, null, true);
        when(checkInRepository.findByIdAndUserId(checkInId, userId)).thenReturn(Optional.of(checkIn));
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(inv -> inv.getArgument(0));

        checkInService.update(checkInId, userId, request);

        verify(insightService).generateFor(any());
    }

    @Test
    void update_doesNotTriggerInsightWhenAlreadyCompleted() {
        UUID checkInId = UUID.randomUUID();
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        CheckIn checkIn = new CheckIn(user, sunday);
        checkIn.setCompleted(true);
        // already completed, sending completed=true again
        var request = new CheckInRequest("Updated wins", null, null, null, null, true);
        when(checkInRepository.findByIdAndUserId(checkInId, userId)).thenReturn(Optional.of(checkIn));
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(inv -> inv.getArgument(0));

        checkInService.update(checkInId, userId, request);

        verify(insightService, never()).generateFor(any());
    }

    @Test
    void update_doesNotTriggerInsightWhenNotMarkedComplete() {
        UUID checkInId = UUID.randomUUID();
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        CheckIn checkIn = new CheckIn(user, sunday);
        var request = new CheckInRequest("Wins", null, null, null, null, null);
        when(checkInRepository.findByIdAndUserId(checkInId, userId)).thenReturn(Optional.of(checkIn));
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(inv -> inv.getArgument(0));

        checkInService.update(checkInId, userId, request);

        verify(insightService, never()).generateFor(any());
    }

    // ── Streak ──────────────────────────────────────────────────────────

    @Test
    void getStreak_returnsZeroWithNoCheckIns() {
        when(checkInRepository.findCompletedWeekStartsByUserIdDesc(userId)).thenReturn(List.of());
        assertEquals(0, checkInService.getStreak(userId));
    }

    @Test
    void getStreak_countsConsecutiveWeeks() {
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        when(checkInRepository.findCompletedWeekStartsByUserIdDesc(userId))
                .thenReturn(List.of(sunday, sunday.minusWeeks(1), sunday.minusWeeks(2)));
        assertEquals(3, checkInService.getStreak(userId));
    }

    @Test
    void getStreak_breaksOnGap() {
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        // Current week + 2 weeks ago (gap at week -1)
        when(checkInRepository.findCompletedWeekStartsByUserIdDesc(userId))
                .thenReturn(List.of(sunday, sunday.minusWeeks(2)));
        assertEquals(1, checkInService.getStreak(userId));
    }

    @Test
    void getStreak_returnsZeroIfCurrentWeekMissing() {
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        // Only last week, not current
        when(checkInRepository.findCompletedWeekStartsByUserIdDesc(userId))
                .thenReturn(List.of(sunday.minusWeeks(1)));
        assertEquals(0, checkInService.getStreak(userId));
    }

    // ── Free Tier Enforcement ──────────────────────────────────────────

    @Test
    void create_blockedWhenFreeTierLimitReached() {
        var request = new CheckInRequest("Wins", null, null, null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(checkInRepository.countCompletedByUserId(userId)).thenReturn(4L);

        ApiException ex = assertThrows(ApiException.class, () -> checkInService.create(userId, request));
        assertEquals(403, ex.getStatus().value());
        assertTrue(ex.getMessage().contains("Free tier limit reached"));
        verify(checkInRepository, never()).save(any());
    }

    @Test
    void create_allowedWhenProUserExceedsFreeTierLimit() {
        user.setSubscriptionStatus("ACTIVE");
        var request = new CheckInRequest("Wins", null, null, null, null, null);
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(checkInRepository.findByUserIdAndWeekStart(userId, sunday)).thenReturn(Optional.empty());
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(inv -> inv.getArgument(0));

        CheckIn result = checkInService.create(userId, request);
        assertNotNull(result);
        // countCompletedByUserId should NOT be called for Pro users
        verify(checkInRepository, never()).countCompletedByUserId(any());
    }

    @Test
    void create_allowedWhenUnderFreeTierLimit() {
        var request = new CheckInRequest("Wins", null, null, null, null, null);
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(checkInRepository.countCompletedByUserId(userId)).thenReturn(3L);
        when(checkInRepository.findByUserIdAndWeekStart(userId, sunday)).thenReturn(Optional.empty());
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(inv -> inv.getArgument(0));

        CheckIn result = checkInService.create(userId, request);
        assertNotNull(result);
    }
}
