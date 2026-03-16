package com.example.roomstatus.service;

import com.example.roomstatus.config.AppProperties;
import com.example.roomstatus.config.CampusCatalogProperties;
import com.example.roomstatus.exception.RoomNotFoundException;
import com.example.roomstatus.integration.google.GoogleCalendarClient;
import com.example.roomstatus.model.Building;
import com.example.roomstatus.model.Room;
import com.example.roomstatus.model.RoomEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "true")
public class GoogleWorkspaceDataService implements CampusDataProvider {

    private final CampusCatalogProperties campusCatalogProperties;
    private final GoogleCalendarClient googleCalendarClient;
    private final AppProperties appProperties;
    private final AtomicReference<Instant> lastSuccessfulSync = new AtomicReference<>();

    public GoogleWorkspaceDataService(
            CampusCatalogProperties campusCatalogProperties,
            GoogleCalendarClient googleCalendarClient,
            AppProperties appProperties
    ) {
        this.campusCatalogProperties = campusCatalogProperties;
        this.googleCalendarClient = googleCalendarClient;
        this.appProperties = appProperties;
    }

    @Override
    public List<Building> getBuildings() {
        Map<String, BuildingAccumulator> buildings = new LinkedHashMap<>();

        for (CampusCatalogProperties.RoomDefinition room : campusCatalogProperties.getRooms()) {
            BuildingAccumulator accumulator = buildings.computeIfAbsent(
                    room.getBuildingId(),
                    ignored -> new BuildingAccumulator(
                            room.getBuildingId(),
                            room.getBuildingName(),
                            room.getAddress()
                    )
            );

            accumulator.addFloor(room.getFloor());
        }

        markSuccessfulSync();

        return buildings.values().stream()
                .map(BuildingAccumulator::toBuilding)
                .toList();
    }

    @Override
    public List<Room> getRooms() {
        LocalDate today = LocalDate.now(ZoneId.of(appProperties.getTimeZone()));

        List<Room> rooms = campusCatalogProperties.getRooms().stream()
                .map(room -> toRoom(room, today, today))
                .toList();

        markSuccessfulSync();
        return rooms;
    }

    @Override
    public Room getRoomByCode(String code) {
        CampusCatalogProperties.RoomDefinition room = findRoomDefinition(code);
        LocalDate today = LocalDate.now(ZoneId.of(appProperties.getTimeZone()));

        Room result = toRoom(room, today, today);
        markSuccessfulSync();
        return result;
    }

    @Override
    public List<RoomEvent> getRoomSchedule(String code, LocalDate start, LocalDate end) {
        CampusCatalogProperties.RoomDefinition room = findRoomDefinition(code);

        List<RoomEvent> events = googleCalendarClient.listEvents(room.getCalendarId(), start, end);
        markSuccessfulSync();
        return events;
    }

    @Override
    public Instant getLastSuccessfulSync() {
        return lastSuccessfulSync.get();
    }

    @Override
    public boolean usesGoogle() {
        return true;
    }

    private Room toRoom(CampusCatalogProperties.RoomDefinition room, LocalDate start, LocalDate end) {
        Building building = new Building(
                room.getBuildingId(),
                room.getBuildingName(),
                room.getAddress(),
                room.getFloor() == null ? List.of() : List.of(room.getFloor())
        );

        List<RoomEvent> schedule = googleCalendarClient.listEvents(room.getCalendarId(), start, end);

        return new Room(
                room.getCode(),
                room.getName(),
                room.getCalendarId(),
                building,
                room.getFloor(),
                room.getCapacity(),
                room.getType(),
                room.isMaintenance(),
                schedule
        );
    }

    private CampusCatalogProperties.RoomDefinition findRoomDefinition(String code) {
        return campusCatalogProperties.getRooms().stream()
                .filter(room -> room.getCode() != null && room.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new RoomNotFoundException(
                        "La salle avec le code '%s' n'existe pas".formatted(code)
                ));
    }

    private void markSuccessfulSync() {
        lastSuccessfulSync.set(Instant.now());
    }

    private static class BuildingAccumulator {
        private final String id;
        private final String name;
        private final String address;
        private final java.util.Set<String> floors = new java.util.LinkedHashSet<>();

        private BuildingAccumulator(String id, String name, String address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }

        private void addFloor(String floor) {
            if (floor != null && !floor.isBlank()) {
                floors.add(floor);
            }
        }

        private Building toBuilding() {
            return new Building(id, name, address, List.copyOf(floors));
        }
    }
}