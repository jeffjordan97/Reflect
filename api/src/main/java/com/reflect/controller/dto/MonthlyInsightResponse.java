package com.reflect.controller.dto;

import com.reflect.domain.MonthlyInsight;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MonthlyInsightResponse(
        UUID id,
        String content,
        LocalDate periodStart,
        LocalDate periodEnd,
        int checkInCount,
        OffsetDateTime createdAt
) {
    public static MonthlyInsightResponse from(MonthlyInsight insight) {
        return new MonthlyInsightResponse(
                insight.getId(),
                insight.getContent(),
                insight.getPeriodStart(),
                insight.getPeriodEnd(),
                insight.getCheckInCount(),
                insight.getCreatedAt()
        );
    }
}
