package com.example.roomstatus.dto.common;

import java.util.List;

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