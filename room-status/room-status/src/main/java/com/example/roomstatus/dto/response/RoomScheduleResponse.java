package com.example.roomstatus.dto.response;

import com.example.roomstatus.dto.common.EventDto;

import java.util.List;
import java.util.Map;

public record RoomScheduleResponse(
        String roomCode,
        Map<String, String> period,
        List<EventDto> events
) {
}