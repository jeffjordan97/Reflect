package com.reflect.controller.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record GoalUpdateRequest(
        @Size(max = 300) String title,
        @Size(max = 2000) String description,
        LocalDate targetDate,
        String status
) {}
