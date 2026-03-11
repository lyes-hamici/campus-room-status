package com.example.roomstatus.controller;

import com.example.roomstatus.dto.request.RoomSearchRequest;
import com.example.roomstatus.dto.response.RoomDetailResponse;
import com.example.roomstatus.dto.response.RoomListResponse;
import com.example.roomstatus.dto.response.RoomScheduleResponse;
import com.example.roomstatus.exception.InvalidRequestException;
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
            @RequestParam(name = "building", required = false) String building,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "capacity_min", required = false) Integer capacityMin,
            @RequestParam(name = "capacity_max", required = false) Integer capacityMax,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "order", required = false) String order
    ) {
        RoomSearchRequest request = new RoomSearchRequest(
                normalize(building),
                normalize(type),
                normalize(status),
                capacityMin,
                capacityMax,
                normalize(sort),
                normalize(order)
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
    public RoomDetailResponse getRoomByCode(@PathVariable("code") String code) {
        return new RoomDetailResponse(
                Instant.now(),
                roomService.getRoomByCode(normalizeRequired(code, "code"))
        );
    }

    @GetMapping("/rooms/{code}/schedule")
    public RoomScheduleResponse getRoomSchedule(
            @PathVariable("code") String code,
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return roomService.getRoomSchedule(normalizeRequired(code, "code"), start, end);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeRequired(String value, String parameterName) {
        String normalized = normalize(value);
        if (normalized == null) {
            throw new InvalidRequestException("Le paramètre '%s' est obligatoire".formatted(parameterName));
        }
        return normalized;
    }
}