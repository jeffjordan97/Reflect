package com.reflect.controller;

import com.reflect.controller.dto.InsightResponse;
import com.reflect.service.InsightService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/insights")
public class InsightController {

    private final InsightService insightService;

    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    /**
     * Get the insight for a specific check-in. Returns 404 if not yet generated.
     * The frontend should poll this after a check-in is marked complete.
     */
    @GetMapping("/check-ins/{checkInId}")
    public ResponseEntity<InsightResponse> getByCheckIn(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID checkInId
    ) {
        return insightService.getByCheckIn(checkInId, userId)
                .map(InsightResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
