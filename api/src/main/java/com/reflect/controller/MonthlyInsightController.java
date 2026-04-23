package com.reflect.controller;

import com.reflect.controller.dto.MonthlyInsightResponse;
import com.reflect.service.MonthlyInsightService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monthly-insights")
public class MonthlyInsightController {

    private final MonthlyInsightService monthlyInsightService;

    public MonthlyInsightController(MonthlyInsightService monthlyInsightService) {
        this.monthlyInsightService = monthlyInsightService;
    }

    @GetMapping("/latest")
    public ResponseEntity<MonthlyInsightResponse> getLatest(@AuthenticationPrincipal UUID userId) {
        return monthlyInsightService.getLatest(userId)
                .map(MonthlyInsightResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<MonthlyInsightResponse>> getAll(@AuthenticationPrincipal UUID userId) {
        List<MonthlyInsightResponse> insights = monthlyInsightService.getAll(userId)
                .stream()
                .map(MonthlyInsightResponse::from)
                .toList();
        return ResponseEntity.ok(insights);
    }
}
