package com.reflect.controller.dto;

import com.reflect.domain.User;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        boolean emailVerified,
        String subscriptionStatus,
        boolean pro,
        boolean remindersEnabled,
        OffsetDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(), user.getEmail(),
                user.getDisplayName(), user.isEmailVerified(),
                user.getSubscriptionStatus(), user.isPro(),
                user.isRemindersEnabled(),
                user.getCreatedAt()
        );
    }
}
