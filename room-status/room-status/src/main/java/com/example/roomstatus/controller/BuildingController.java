package com.example.roomstatus.controller;

import com.example.roomstatus.dto.response.BuildingListResponse;
import com.example.roomstatus.service.BuildingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping("/api/v1/buildings")
    public BuildingListResponse getBuildings() {
        return new BuildingListResponse(
                Instant.now(),
                buildingService.getBuildings()
        );
    }
}