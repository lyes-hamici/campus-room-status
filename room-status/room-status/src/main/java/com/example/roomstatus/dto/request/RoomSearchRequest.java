package com.example.roomstatus.dto.request;

public record RoomSearchRequest(
        String building,
        String type,
        String status,
        Integer capacityMin,
        Integer capacityMax,
        String sort,
        String order
) {
    public static RoomSearchRequest empty() {
        return new RoomSearchRequest(null, null, null, null, null, null, null);
    }
}