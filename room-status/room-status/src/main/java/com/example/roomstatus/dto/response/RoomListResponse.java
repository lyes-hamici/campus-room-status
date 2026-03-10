package com.example.roomstatus.dto.response;

import com.example.roomstatus.dto.common.RoomDto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record RoomListResponse(
        Instant timestamp,
        Map<String, Object> filters,
        int count,
        List<RoomDto> rooms
) {
}