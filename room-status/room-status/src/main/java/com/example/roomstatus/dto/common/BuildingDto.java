package com.example.roomstatus.dto.common;

import java.util.List;

public record BuildingDto(
        String id,
        String name,
        String address,
        List<String> floors
) {
}