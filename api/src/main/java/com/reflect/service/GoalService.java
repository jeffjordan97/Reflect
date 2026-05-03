package com.reflect.service;

import com.reflect.controller.dto.GoalRequest;
import com.reflect.controller.dto.GoalUpdateRequest;
import com.reflect.domain.Goal;
import com.reflect.domain.User;
import com.reflect.exception.ApiException;
import com.reflect.repository.GoalRepository;
import com.reflect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GoalService {

    private static final int MAX_ACTIVE_GOALS = 7;
    private static final Set<String> VALID_HORIZONS = Set.of("SHORT", "MEDIUM", "LONG");

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Goal> getAll(UUID userId) {
        return goalRepository.findByUserIdOrderBySortOrderAsc(userId);
    }

    @Transactional(readOnly = true)
    public List<Goal> getActive(UUID userId) {
        return goalRepository.findByUserIdAndStatusOrderBySortOrderAsc(userId, "ACTIVE");
    }

    @Transactional(readOnly = true)
    public Goal getById(UUID goalId, UUID userId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> ApiException.notFound("Goal not found"));
        if (!goal.getUser().getId().equals(userId)) {
            throw ApiException.notFound("Goal not found");
        }
        return goal;
    }

    @Transactional
    public Goal create(UUID userId, GoalRequest request) {
        User user = requirePro(userId);

        if (!VALID_HORIZONS.contains(request.horizon())) {
            throw ApiException.badRequest("Horizon must be one of: SHORT, MEDIUM, LONG");
        }

        long activeCount = goalRepository.countByUserIdAndStatus(userId, "ACTIVE");
        if (activeCount >= MAX_ACTIVE_GOALS) {
            throw ApiException.badRequest("Maximum of " + MAX_ACTIVE_GOALS + " active goals reached");
        }

        Goal goal = new Goal(user, request.title(), request.horizon());
        goal.setDescription(request.description());
        goal.setTargetDate(request.targetDate());

        return goalRepository.save(goal);
    }

    @Transactional
    public Goal update(UUID goalId, UUID userId, GoalUpdateRequest request) {
        requirePro(userId);
        Goal goal = getById(goalId, userId);

        if (request.title() != null) {
            goal.setTitle(request.title());
        }
        if (request.description() != null) {
            goal.setDescription(request.description());
        }
        if (request.targetDate() != null) {
            goal.setTargetDate(request.targetDate());
        }
        if (request.status() != null) {
            applyStatusTransition(goal, request.status());
        }

        return goalRepository.save(goal);
    }

    @Transactional
    public Goal complete(UUID goalId, UUID userId) {
        requirePro(userId);
        Goal goal = getById(goalId, userId);
        if (!goal.isActive()) {
            throw ApiException.badRequest("Only active goals can be completed");
        }
        goal.complete();
        return goalRepository.save(goal);
    }

    @Transactional
    public Goal release(UUID goalId, UUID userId) {
        requirePro(userId);
        Goal goal = getById(goalId, userId);
        if (!goal.isActive()) {
            throw ApiException.badRequest("Only active goals can be released");
        }
        goal.release();
        return goalRepository.save(goal);
    }

    @Transactional
    public void delete(UUID goalId, UUID userId) {
        requirePro(userId);
        Goal goal = getById(goalId, userId);
        goalRepository.delete(goal);
    }

    private User requirePro(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));
        if (!user.isPro()) {
            throw ApiException.forbidden("Pro subscription required");
        }
        return user;
    }

    private void applyStatusTransition(Goal goal, String targetStatus) {
        switch (targetStatus) {
            case "COMPLETED" -> {
                if (!goal.isActive()) {
                    throw ApiException.badRequest("Only active goals can be completed");
                }
                goal.complete();
            }
            case "RELEASED" -> {
                if (!goal.isActive()) {
                    throw ApiException.badRequest("Only active goals can be released");
                }
                goal.release();
            }
            case "PAUSED" -> {
                if (!goal.isActive()) {
                    throw ApiException.badRequest("Only active goals can be paused");
                }
                goal.pause();
            }
            case "ACTIVE" -> {
                if (!"PAUSED".equals(goal.getStatus())) {
                    throw ApiException.badRequest("Only paused goals can be resumed");
                }
                goal.resume();
            }
            default -> throw ApiException.badRequest("Invalid status: " + targetStatus);
        }
    }
}
