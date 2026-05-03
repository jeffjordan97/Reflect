package com.reflect.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record GoalRequest(
        @NotBlank @Size(max = 300) String title,
        @Size(max = 2000) String description,
        @NotBlank String horizon,
        LocalDate targetDate
) {}
