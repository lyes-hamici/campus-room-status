package com.example.roomstatus.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RoomDto(
        String code,
        String name,
        BuildingDto building,
        String floor,
        int capacity,
        String type,
        String status,
        EventDto currentEvent,
        EventDto nextEvent,
        List<EventDto> scheduleToday
) {
}