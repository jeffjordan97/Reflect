package com.reflect.controller.dto;

public record AuthResponse(
        String accessToken,
        long expiresIn
) {}
