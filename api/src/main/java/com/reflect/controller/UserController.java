package com.reflect.controller;

import com.reflect.controller.dto.ChangePasswordRequest;
import com.reflect.controller.dto.UpdateProfileRequest;
import com.reflect.controller.dto.UserResponse;
import com.reflect.exception.ApiException;
import com.reflect.repository.UserRepository;
import com.reflect.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UUID userId) {
        return userRepository.findById(userId)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ApiException.notFound("User not found"));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        var updatedUser = authService.updateProfile(userId, request.displayName());
        return ResponseEntity.ok(UserResponse.from(updatedUser));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(userId, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/reminders")
    public ResponseEntity<UserResponse> updateReminders(
            @AuthenticationPrincipal UUID userId,
            @RequestBody Map<String, Boolean> body
    ) {
        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            throw ApiException.badRequest("'enabled' field is required");
        }
        var user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));
        user.setRemindersEnabled(enabled);
        userRepository.save(user);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}
