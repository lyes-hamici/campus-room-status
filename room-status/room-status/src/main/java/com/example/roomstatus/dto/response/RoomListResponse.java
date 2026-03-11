package com.example.roomstatus.dto.response;

import com.example.roomstatus.dto.common.FilterDto;
import com.example.roomstatus.dto.common.RoomSummaryDto;

import java.time.Instant;
import java.util.List;

public record RoomListResponse(
        Instant timestamp,
        FilterDto filters,
        int count,
        List<RoomSummaryDto> rooms
) {
}