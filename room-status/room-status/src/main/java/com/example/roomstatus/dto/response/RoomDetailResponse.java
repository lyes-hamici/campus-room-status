package com.example.roomstatus.dto.response;

import com.example.roomstatus.dto.common.RoomDto;

import java.time.Instant;

public record RoomDetailResponse(
        Instant timestamp,
        RoomDto room
) {
}