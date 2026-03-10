package com.example.roomstatus.controller;

import com.example.roomstatus.dto.response.RoomDetailResponse;
import com.example.roomstatus.dto.response.RoomListResponse;
import com.example.roomstatus.dto.response.RoomScheduleResponse;
import com.example.roomstatus.service.RoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/api/v1/rooms")
    public RoomListResponse getRooms(
            @RequestParam(required = false) String building,
            @RequestParam(required = false) String status
    ) {
        Map<String, Object> filters = Map.of(
                "building", building,
                "status", status
        );

        var rooms = roomService.getRooms(building, status);

        return new RoomListResponse(
                Instant.now(),
                filters,
                rooms.size(),
                rooms
        );
    }

    @GetMapping("/api/v1/rooms/{code}")
    public RoomDetailResponse getRoomByCode(@PathVariable String code) {
        return new RoomDetailResponse(
                Instant.now(),
                roomService.getRoomByCode(code)
        );
    }

    @GetMapping("/api/v1/rooms/{code}/schedule")
    public RoomScheduleResponse getRoomSchedule(
            @PathVariable String code,
            @RequestParam String start,
            @RequestParam String end
    ) {
        var result = roomService.getRoomSchedule(code, start, end);

        @SuppressWarnings("unchecked")
        List<com.example.roomstatus.dto.common.EventDto> events =
                (List<com.example.roomstatus.dto.common.EventDto>) result.get("events");

        return new RoomScheduleResponse(
                (String) result.get("roomCode"),
                Map.of(
                        "start", (String) result.get("start"),
                        "end", (String) result.get("end")
                ),
                events
        );
    }
}