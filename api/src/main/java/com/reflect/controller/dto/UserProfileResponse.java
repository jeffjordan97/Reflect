package com.reflect.controller.dto;

import com.reflect.domain.UserProfile;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String profession,
        String industry,
        String roleLevel,
        List<String> focusAreas,
        String bioContext,
        OffsetDateTime updatedAt
) {
    public static UserProfileResponse from(UserProfile profile) {
        List<String> areas = profile.getFocusAreas() != null
                ? Arrays.asList(profile.getFocusAreas())
                : List.of();
        return new UserProfileResponse(
                profile.getId(),
                profile.getProfession(),
                profile.getIndustry(),
                profile.getRoleLevel(),
                areas,
                profile.getBioContext(),
                profile.getUpdatedAt()
        );
    }
}
