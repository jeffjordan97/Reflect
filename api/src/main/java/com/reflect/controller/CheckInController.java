package com.reflect.controller;

import com.reflect.controller.dto.CheckInRequest;
import com.reflect.controller.dto.CheckInResponse;
import com.reflect.domain.CheckIn;
import com.reflect.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/check-ins")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @GetMapping
    public ResponseEntity<Page<CheckInResponse>> list(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CheckInResponse> results = checkInService
                .list(userId, PageRequest.of(page, size))
                .map(CheckInResponse::from);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/current")
    public ResponseEntity<CheckInResponse> getCurrent(@AuthenticationPrincipal UUID userId) {
        return checkInService.getCurrent(userId)
                .map(CheckInResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckInResponse> getById(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id
    ) {
        return checkInService.getById(id, userId)
                .map(CheckInResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CheckInResponse> create(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody CheckInRequest request
    ) {
        CheckIn checkIn = checkInService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CheckInResponse.from(checkIn));
    }

    @GetMapping("/streak")
    public ResponseEntity<Map<String, Integer>> getStreak(@AuthenticationPrincipal UUID userId) {
        int streak = checkInService.getStreak(userId);
        return ResponseEntity.ok(Map.of("streak", streak));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheckInResponse> update(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody CheckInRequest request
    ) {
        CheckIn checkIn = checkInService.update(id, userId, request);
        return ResponseEntity.ok(CheckInResponse.from(checkIn));
    }
}
