package com.reflect.controller.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public record UserProfileRequest(
        @Size(max = 200) String profession,
        @Size(max = 100) String industry,
        @Size(max = 50) String roleLevel,
        List<@Size(max = 50) String> focusAreas,
        @Size(max = 1000) String bioContext
) {}
