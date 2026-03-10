package com.example.roomstatus.dto.response;

import java.time.Instant;

public record HealthResponse(
        String status,
        String version,
        boolean googleAdminApiConnected,
        boolean googleCalendarApiConnected,
        Instant lastSync,
        long responseTimeMs
) {
}