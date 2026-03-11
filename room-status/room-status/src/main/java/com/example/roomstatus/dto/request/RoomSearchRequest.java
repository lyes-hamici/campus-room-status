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

    public RoomSearchRequest normalized() {
        return new RoomSearchRequest(
                trimToNull(building),
                trimToNull(type),
                trimToNull(status),
                capacityMin,
                capacityMax,
                trimToNull(sort),
                trimToNull(order)
        );
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}