package com.reflect.controller;

import com.reflect.controller.dto.GoalRequest;
import com.reflect.controller.dto.GoalResponse;
import com.reflect.controller.dto.GoalUpdateRequest;
import com.reflect.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAll(@AuthenticationPrincipal UUID userId) {
        List<GoalResponse> goals = goalService.getAll(userId).stream()
                .map(GoalResponse::from)
                .toList();
        return ResponseEntity.ok(goals);
    }

    @PostMapping
    public ResponseEntity<GoalResponse> create(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody GoalRequest request
    ) {
        var goal = goalService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(GoalResponse.from(goal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getById(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id
    ) {
        var goal = goalService.getById(id, userId);
        return ResponseEntity.ok(GoalResponse.from(goal));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GoalResponse> update(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody GoalUpdateRequest request
    ) {
        var goal = goalService.update(id, userId, request);
        return ResponseEntity.ok(GoalResponse.from(goal));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<GoalResponse> complete(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id
    ) {
        var goal = goalService.complete(id, userId);
        return ResponseEntity.ok(GoalResponse.from(goal));
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<GoalResponse> release(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id
    ) {
        var goal = goalService.release(id, userId);
        return ResponseEntity.ok(GoalResponse.from(goal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id
    ) {
        goalService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
