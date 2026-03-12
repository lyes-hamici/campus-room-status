package com.example.roomstatus.service;

import com.example.roomstatus.config.AppProperties;
import com.example.roomstatus.dto.response.HealthResponse;
import com.example.roomstatus.integration.google.GoogleConnectionChecker;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class HealthService {

    private final AppProperties properties;
    private final GoogleConnectionChecker googleConnectionChecker;
    private final CampusDataProvider campusDataProvider;

    public HealthService(
            AppProperties properties,
            GoogleConnectionChecker googleConnectionChecker,
            CampusDataProvider campusDataProvider
    ) {
        this.properties = properties;
        this.googleConnectionChecker = googleConnectionChecker;
        this.campusDataProvider = campusDataProvider;
    }

    public HealthResponse getHealth() {
        long start = System.nanoTime();

        boolean googleAdminApiConnected = properties.isEnabled() && googleConnectionChecker.isAdminDirectoryConnected();
        boolean googleCalendarApiConnected = properties.isEnabled() && googleConnectionChecker.isCalendarConnected();

        long responseTimeMs = Duration.ofNanos(System.nanoTime() - start).toMillis();

        String status = (!properties.isEnabled() || (googleAdminApiConnected && googleCalendarApiConnected))
                ? "healthy"
                : "degraded";

        return new HealthResponse(
                status,
                "1.0.0",
                googleAdminApiConnected,
                googleCalendarApiConnected,
                campusDataProvider.getLastSuccessfulSync(),
                responseTimeMs
        );
    }
}