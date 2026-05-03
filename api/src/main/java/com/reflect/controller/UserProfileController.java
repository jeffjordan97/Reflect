package com.reflect.controller;

import com.reflect.controller.dto.UserProfileRequest;
import com.reflect.controller.dto.UserProfileResponse;
import com.reflect.exception.ApiException;
import com.reflect.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/me/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UUID userId) {
        return userProfileService.getByUserId(userId)
                .map(UserProfileResponse::from)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ApiException.notFound("Profile not found"));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> createOrUpdateProfile(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody UserProfileRequest request
    ) {
        var profile = userProfileService.createOrUpdate(userId, request);
        return ResponseEntity.ok(UserProfileResponse.from(profile));
    }
}
