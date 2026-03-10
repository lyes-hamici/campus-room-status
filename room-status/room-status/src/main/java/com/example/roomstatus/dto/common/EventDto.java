package com.example.roomstatus.dto.common;

import java.time.Instant;

public record EventDto(
        String title,
        Instant start,
        Instant end,
        String organizer
) {
}