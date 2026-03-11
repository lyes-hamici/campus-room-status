package com.example.roomstatus.model;

import java.time.Instant;

public record RoomEvent(
        String title,
        Instant start,
        Instant end,
        String organizer
) {
}