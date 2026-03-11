package com.example.roomstatus.model;

import java.util.List;

public record Building(
        String id,
        String name,
        String address,
        List<String> floors
) {
}