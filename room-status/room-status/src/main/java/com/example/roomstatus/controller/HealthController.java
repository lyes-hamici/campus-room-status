package com.example.roomstatus.controller;

import com.example.roomstatus.dto.response.HealthResponse;
import com.example.roomstatus.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de supervision minimal pour verifier l'etat global de l'application.
 */
@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/api/v1/health")
    public HealthResponse health() {
        return healthService.getHealth();
    }
}
