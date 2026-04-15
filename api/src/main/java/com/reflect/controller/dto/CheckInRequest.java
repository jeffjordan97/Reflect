package com.reflect.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CheckInRequest(
        @Size(max = 5000) String wins,
        @Size(max = 5000) String friction,
        @Min(1) @Max(10) Short energyRating,
        @Size(max = 5000) String signalMoment,
        @Size(max = 5000) String intentions,
        Boolean completed
) {}
