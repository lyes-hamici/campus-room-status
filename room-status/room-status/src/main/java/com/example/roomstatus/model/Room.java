package com.example.roomstatus.model;

import java.util.List;

public record Room(
        String code,
        String name,
        String calendarId,
        Building building,
        String floor,
        int capacity,
        String type,
        boolean maintenance,
        List<RoomEvent> scheduleToday
) {
}