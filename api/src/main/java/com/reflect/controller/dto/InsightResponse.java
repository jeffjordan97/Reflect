package com.reflect.controller.dto;

import com.reflect.domain.Insight;
import java.time.OffsetDateTime;
import java.util.UUID;

public record InsightResponse(
        UUID id,
        UUID checkInId,
        String content,
        OffsetDateTime createdAt
) {
    public static InsightResponse from(Insight insight) {
        return new InsightResponse(
                insight.getId(),
                insight.getCheckIn().getId(),
                insight.getContent(),
                insight.getCreatedAt()
        );
    }
}
