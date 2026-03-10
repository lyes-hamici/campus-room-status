package com.example.roomstatus.service;

import com.example.roomstatus.dto.response.HealthResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class HealthService {

    public HealthResponse getHealth() {
        return new HealthResponse(
                "healthy",
                "1.0.0",
                false,
                false,
                null,
                0
        );
    }
}