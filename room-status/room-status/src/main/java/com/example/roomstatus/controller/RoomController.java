package com.example.roomstatus.controller;

import com.example.roomstatus.dto.request.RoomSearchRequest;
import com.example.roomstatus.dto.response.RoomDetailResponse;
import com.example.roomstatus.dto.response.RoomListResponse;
import com.example.roomstatus.dto.response.RoomScheduleResponse;
import com.example.roomstatus.service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/rooms")
    public RoomListResponse getRooms(
            @RequestParam(required = false) String building,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(name = "capacity_min", required = false) Integer capacityMin,
            @RequestParam(name = "capacity_max", required = false) Integer capacityMax,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order
    ) {
        RoomSearchRequest request = new RoomSearchRequest(
                building,
                type,
                status,
                capacityMin,
                capacityMax,
                sort,
                order
        );

        var rooms = roomService.getRooms(request);

        return new RoomListResponse(
                Instant.now(),
                roomService.toFilterDto(request),
                rooms.size(),
                rooms
        );
    }

    @GetMapping("/rooms/{code}")
    public RoomDetailResponse getRoomByCode(@PathVariable String code) {
        return new RoomDetailResponse(
                Instant.now(),
                roomService.getRoomByCode(code)
        );
    }

    @GetMapping("/rooms/{code}/schedule")
    public RoomScheduleResponse getRoomSchedule(
            @PathVariable String code,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return roomService.getRoomSchedule(code, start, end);
    }
}