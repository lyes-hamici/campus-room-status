package com.example.roomstatus.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RoomSummaryDto(
        String code,
        String name,
        BuildingDto building,
        String floor,
        int capacity,
        String type,
        String status,
        EventDto currentEvent,
        EventDto nextEvent
) {
}