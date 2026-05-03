package com.reflect.service;

import com.reflect.controller.dto.GoalRequest;
import com.reflect.controller.dto.GoalUpdateRequest;
import com.reflect.domain.Goal;
import com.reflect.domain.User;
import com.reflect.exception.ApiException;
import com.reflect.repository.GoalRepository;
import com.reflect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock private GoalRepository goalRepository;
    @Mock private UserRepository userRepository;

    private GoalService goalService;
    private UUID userId;
    private User proUser;
    private User freeUser;

    @BeforeEach
    void setUp() throws Exception {
        goalService = new GoalService(goalRepository, userRepository);
        userId = UUID.randomUUID();

        proUser = new User("pro@example.com", "hash", "Pro User");
        proUser.setSubscriptionStatus("ACTIVE");
        setId(proUser, userId);

        freeUser = new User("free@example.com", "hash", "Free User");
        setId(freeUser, userId);
    }

    private static void setId(User user, UUID id) throws Exception {
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
    }

    // ── Create ─────────────────────────────────────────────────────────

    @Test
    void create_succeedsForProUser() {
        var request = new GoalRequest("Learn Rust", "Systems programming", "MEDIUM", LocalDate.of(2026, 12, 31));
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(goalRepository.countByUserIdAndStatus(userId, "ACTIVE")).thenReturn(0L);
        when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

        Goal result = goalService.create(userId, request);

        assertNotNull(result);
        assertEquals("Learn Rust", result.getTitle());
        assertEquals("Systems programming", result.getDescription());
        assertEquals("MEDIUM", result.getHorizon());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(LocalDate.of(2026, 12, 31), result.getTargetDate());
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void create_throwsForbiddenForFreeUser() {
        var request = new GoalRequest("Goal", null, "SHORT", null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(freeUser));

        ApiException ex = assertThrows(ApiException.class, () -> goalService.create(userId, request));
        assertEquals(403, ex.getStatus().value());
        assertTrue(ex.getMessage().contains("Pro subscription required"));
        verify(goalRepository, never()).save(any());
    }

    @Test
    void create_throwsBadRequestAt7ActiveGoals() {
        var request = new GoalRequest("Goal 8", null, "SHORT", null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(goalRepository.countByUserIdAndStatus(userId, "ACTIVE")).thenReturn(7L);

        ApiException ex = assertThrows(ApiException.class, () -> goalService.create(userId, request));
        assertEquals(400, ex.getStatus().value());
        assertTrue(ex.getMessage().contains("7"));
        verify(goalRepository, never()).save(any());
    }

    @Test
    void create_validatesHorizon() {
        var request = new GoalRequest("Goal", null, "INVALID", null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));

        ApiException ex = assertThrows(ApiException.class, () -> goalService.create(userId, request));
        assertEquals(400, ex.getStatus().value());
        assertTrue(ex.getMessage().contains("Horizon"));
    }

    // ── Complete / Release ─────────────────────────────────────────────

    @Test
    void complete_transitionsStatusToCompleted() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal(proUser, "Goal", "SHORT");
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

        Goal result = goalService.complete(goalId, userId);

        assertEquals("COMPLETED", result.getStatus());
        assertNotNull(result.getCompletedAt());
    }

    @Test
    void release_transitionsStatusToReleased() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal(proUser, "Goal", "LONG");
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

        Goal result = goalService.release(goalId, userId);

        assertEquals("RELEASED", result.getStatus());
        assertNotNull(result.getReleasedAt());
    }

    @Test
    void complete_throwsBadRequestWhenGoalNotActive() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal(proUser, "Goal", "SHORT");
        goal.pause();
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        ApiException ex = assertThrows(ApiException.class, () -> goalService.complete(goalId, userId));
        assertEquals(400, ex.getStatus().value());
    }

    // ── Update ─────────────────────────────────────────────────────────

    @Test
    void update_modifiesProvidedFields() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal(proUser, "Old Title", "SHORT");
        var request = new GoalUpdateRequest("New Title", "New desc", LocalDate.of(2027, 1, 1), null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

        Goal result = goalService.update(goalId, userId, request);

        assertEquals("New Title", result.getTitle());
        assertEquals("New desc", result.getDescription());
        assertEquals(LocalDate.of(2027, 1, 1), result.getTargetDate());
    }

    @Test
    void update_skipsNullFields() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal(proUser, "Keep This", "MEDIUM");
        goal.setDescription("Keep description");
        var request = new GoalUpdateRequest(null, null, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

        Goal result = goalService.update(goalId, userId, request);

        assertEquals("Keep This", result.getTitle());
        assertEquals("Keep description", result.getDescription());
    }

    @Test
    void update_appliesStatusTransitionViaPatch() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal(proUser, "Goal", "SHORT");
        var request = new GoalUpdateRequest(null, null, null, "PAUSED");

        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

        Goal result = goalService.update(goalId, userId, request);

        assertEquals("PAUSED", result.getStatus());
    }

    @Test
    void update_resumeFromPaused() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal(proUser, "Goal", "SHORT");
        goal.pause();
        var request = new GoalUpdateRequest(null, null, null, "ACTIVE");

        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

        Goal result = goalService.update(goalId, userId, request);

        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void update_rejectsResumeFromNonPaused() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal(proUser, "Goal", "SHORT");
        // Goal is ACTIVE, trying to "resume" (set to ACTIVE) should fail
        var request = new GoalUpdateRequest(null, null, null, "ACTIVE");

        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        ApiException ex = assertThrows(ApiException.class,
                () -> goalService.update(goalId, userId, request));
        assertEquals(400, ex.getStatus().value());
    }

    // ── getById ────────────────────────────────────────────────────────

    @Test
    void getById_throwsNotFoundForWrongUser() {
        UUID goalId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        Goal goal = new Goal(proUser, "Goal", "SHORT");
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        ApiException ex = assertThrows(ApiException.class,
                () -> goalService.getById(goalId, otherUserId));
        assertEquals(404, ex.getStatus().value());
    }

    @Test
    void getById_throwsNotFoundForMissingGoal() {
        UUID goalId = UUID.randomUUID();
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> goalService.getById(goalId, userId));
        assertEquals(404, ex.getStatus().value());
    }

    // ── Delete ─────────────────────────────────────────────────────────

    @Test
    void delete_removesGoal() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal(proUser, "Goal", "SHORT");
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        goalService.delete(goalId, userId);

        verify(goalRepository).delete(goal);
    }

    @Test
    void delete_throwsForbiddenForFreeUser() {
        UUID goalId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(freeUser));

        ApiException ex = assertThrows(ApiException.class,
                () -> goalService.delete(goalId, userId));
        assertEquals(403, ex.getStatus().value());
    }

    // ── getAll / getActive ─────────────────────────────────────────────

    @Test
    void getAll_returnsAllGoals() {
        Goal g1 = new Goal(proUser, "Goal 1", "SHORT");
        Goal g2 = new Goal(proUser, "Goal 2", "LONG");
        when(goalRepository.findByUserIdOrderBySortOrderAsc(userId)).thenReturn(List.of(g1, g2));

        List<Goal> result = goalService.getAll(userId);

        assertEquals(2, result.size());
    }

    @Test
    void getActive_returnsOnlyActiveGoals() {
        Goal g1 = new Goal(proUser, "Active Goal", "SHORT");
        when(goalRepository.findByUserIdAndStatusOrderBySortOrderAsc(userId, "ACTIVE"))
                .thenReturn(List.of(g1));

        List<Goal> result = goalService.getActive(userId);

        assertEquals(1, result.size());
        assertEquals("Active Goal", result.get(0).getTitle());
    }
}
