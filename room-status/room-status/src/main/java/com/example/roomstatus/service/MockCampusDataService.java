package com.example.roomstatus.service;

import com.example.roomstatus.exception.RoomNotFoundException;
import com.example.roomstatus.model.Building;
import com.example.roomstatus.model.Room;
import com.example.roomstatus.model.RoomEvent;
import com.example.roomstatus.util.DateTimeUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "false", matchIfMissing = true)
public class MockCampusDataService implements CampusDataProvider {

    @Override
    public List<Building> getBuildings() {
        return List.of(
                new Building(
                        "BAT-A",
                        "Bâtiment A - Informatique",
                        "10 rue de l'Innovation",
                        List.of("RDC", "1", "2")
                ),
                new Building(
                        "BAT-B",
                        "Bâtiment B - Général",
                        "12 rue de l'Innovation",
                        List.of("RDC", "1")
                )
        );
    }

    @Override
    public List<Room> getRooms() {
        java.time.Instant referenceNow = java.time.Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

        Building buildingA = getBuildings().get(0);
        Building buildingB = getBuildings().get(1);

        RoomEvent amphiCurrent = new RoomEvent(
                "Cours Architecture Logicielle",
                referenceNow.minus(30, java.time.temporal.ChronoUnit.MINUTES),
                referenceNow.plus(60, java.time.temporal.ChronoUnit.MINUTES),
                "M. Dupont"
        );

        RoomEvent amphiNext = new RoomEvent(
                "Cours Base de données",
                referenceNow.plus(90, java.time.temporal.ChronoUnit.MINUTES),
                referenceNow.plus(180, java.time.temporal.ChronoUnit.MINUTES),
                "Mme Martin"
        );

        RoomEvent labUpcoming = new RoomEvent(
                "TP DevOps",
                referenceNow.plus(20, java.time.temporal.ChronoUnit.MINUTES),
                referenceNow.plus(120, java.time.temporal.ChronoUnit.MINUTES),
                "M. Bernard"
        );

        RoomEvent labAvailableLater = new RoomEvent(
                "TP Java",
                referenceNow.plus(120, java.time.temporal.ChronoUnit.MINUTES),
                referenceNow.plus(210, java.time.temporal.ChronoUnit.MINUTES),
                "Mme Durand"
        );

        return List.of(
                new Room(
                        "AMPHI-A",
                        "Amphithéâtre A",
                        "amphi-a@resource.calendar.google.com",
                        buildingA,
                        "1",
                        150,
                        "Cours magistral",
                        false,
                        List.of(amphiCurrent, amphiNext)
                ),
                new Room(
                        "LAB-INFO-1",
                        "Laboratoire Informatique 1",
                        "lab-info-1@resource.calendar.google.com",
                        buildingA,
                        "2",
                        30,
                        "TP",
                        false,
                        List.of(labUpcoming)
                ),
                new Room(
                        "LAB-INFO-2",
                        "Laboratoire Informatique 2",
                        "lab-info-2@resource.calendar.google.com",
                        buildingA,
                        "2",
                        24,
                        "TP",
                        false,
                        List.of(labAvailableLater)
                ),
                new Room(
                        "MEETING-B1",
                        "Salle de réunion B1",
                        "meeting-b1@resource.calendar.google.com",
                        buildingB,
                        "RDC",
                        12,
                        "Réunion",
                        true,
                        List.of()
                )
        );
    }

    @Override
    public Room getRoomByCode(String code) {
        return getRooms().stream()
                .filter(room -> room.code().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new RoomNotFoundException(
                        "La salle avec le code '%s' n'existe pas".formatted(code)
                ));
    }

    @Override
    public List<RoomEvent> getRoomSchedule(String code, LocalDate start, LocalDate end) {
        return getRoomByCode(code).scheduleToday().stream()
                .filter(event -> DateTimeUtils.isWithinDateRange(event, start, end))
                .toList();
    }
}