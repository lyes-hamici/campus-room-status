package com.example.roomstatus.dto.response;

import com.example.roomstatus.dto.common.BuildingDto;

import java.time.Instant;
import java.util.List;

public record BuildingListResponse(
        Instant timestamp,
        List<BuildingDto> buildings
) {
}