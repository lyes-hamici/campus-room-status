package com.example.roomstatus.service;

import com.example.roomstatus.dto.common.BuildingDto;
import com.example.roomstatus.dto.common.EventDto;
import com.example.roomstatus.dto.common.RoomDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class RoomService {

    public List<RoomDto> getRooms(String building, String status) {
        BuildingDto buildingDto = new BuildingDto(
                "BAT-A",
                "Bâtiment A - Informatique",
                "10 rue de l'Innovation",
                List.of("RDC", "1", "2")
        );

        EventDto nextEvent = new EventDto(
                "TP DevOps",
                Instant.parse("2025-01-23T16:00:00Z"),
                Instant.parse("2025-01-23T18:00:00Z"),
                "M. Bernard"
        );

        RoomDto room = new RoomDto(
                "LAB-INFO-1",
                "Laboratoire Informatique 1",
                buildingDto,
                "2",
                30,
                "TP",
                "available",
                null,
                nextEvent,
                List.of()
        );

        return List.of(room);
    }

    public RoomDto getRoomByCode(String code) {
        BuildingDto buildingDto = new BuildingDto(
                "BAT-A",
                "Bâtiment A - Informatique",
                "10 rue de l'Innovation",
                List.of("RDC", "1", "2")
        );

        EventDto currentEvent = new EventDto(
                "Cours Architecture Logicielle",
                Instant.parse("2025-01-23T14:00:00Z"),
                Instant.parse("2025-01-23T16:00:00Z"),
                "M. Dupont"
        );

        EventDto nextEvent = new EventDto(
                "Cours Base de données",
                Instant.parse("2025-01-23T16:30:00Z"),
                Instant.parse("2025-01-23T18:30:00Z"),
                "Mme Martin"
        );

        return new RoomDto(
                code,
                "Amphithéâtre A",
                buildingDto,
                "1",
                150,
                "Cours magistral",
                "occupied",
                currentEvent,
                nextEvent,
                List.of(currentEvent, nextEvent)
        );
    }

    public Map<String, Object> getRoomSchedule(String code, String start, String end) {
        EventDto event = new EventDto(
                "Cours Architecture Logicielle",
                Instant.parse("2025-01-23T14:00:00Z"),
                Instant.parse("2025-01-23T16:00:00Z"),
                "M. Dupont"
        );

        return Map.of(
                "roomCode", code,
                "start", start,
                "end", end,
                "events", List.of(event)
        );
    }
}