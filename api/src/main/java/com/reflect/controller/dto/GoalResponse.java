package com.reflect.controller.dto;

import com.reflect.domain.Goal;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record GoalResponse(
        UUID id,
        String title,
        String description,
        String horizon,
        String status,
        LocalDate targetDate,
        int sortOrder,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime completedAt,
        OffsetDateTime releasedAt
) {
    public static GoalResponse from(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getHorizon(),
                goal.getStatus(),
                goal.getTargetDate(),
                goal.getSortOrder(),
                goal.getCreatedAt(),
                goal.getUpdatedAt(),
                goal.getCompletedAt(),
                goal.getReleasedAt()
        );
    }
}
