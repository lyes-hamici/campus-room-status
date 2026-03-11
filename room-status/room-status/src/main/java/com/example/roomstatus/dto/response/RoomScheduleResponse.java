package com.example.roomstatus.dto.response;

import com.example.roomstatus.dto.common.EventDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RoomScheduleResponse(
        String roomCode,
        Period period,
        List<EventDto> events
) {
    public record Period(
            LocalDate start,
            LocalDate end
    ) {
    }
}