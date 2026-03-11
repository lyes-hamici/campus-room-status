package com.example.roomstatus.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HealthResponse(
        String status,
        String version,
        boolean googleAdminApiConnected,
        boolean googleCalendarApiConnected,
        Instant lastSync,
        long responseTimeMs
) {
}