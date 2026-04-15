package com.reflect.controller.dto;

import com.reflect.domain.CheckIn;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CheckInResponse(
        UUID id,
        LocalDate weekStart,
        String wins,
        String friction,
        Short energyRating,
        String signalMoment,
        String intentions,
        boolean completed,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static CheckInResponse from(CheckIn checkIn) {
        return new CheckInResponse(
                checkIn.getId(), checkIn.getWeekStart(), checkIn.getWins(),
                checkIn.getFriction(), checkIn.getEnergyRating(), checkIn.getSignalMoment(),
                checkIn.getIntentions(), checkIn.isCompleted(),
                checkIn.getCreatedAt(), checkIn.getUpdatedAt()
        );
    }
}
