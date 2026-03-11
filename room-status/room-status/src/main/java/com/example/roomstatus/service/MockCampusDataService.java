package com.example.roomstatus.service;

import com.example.roomstatus.model.Building;
import com.example.roomstatus.model.Room;
import com.example.roomstatus.model.RoomEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MockCampusDataService {

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

    public List<Room> getRooms() {
        Instant referenceNow = Instant.now().truncatedTo(ChronoUnit.MINUTES);

        Building buildingA = getBuildings().get(0);
        Building buildingB = getBuildings().get(1);

        RoomEvent amphiCurrent = new RoomEvent(
                "Cours Architecture Logicielle",
                referenceNow.minus(30, ChronoUnit.MINUTES),
                referenceNow.plus(60, ChronoUnit.MINUTES),
                "M. Dupont"
        );

        RoomEvent amphiNext = new RoomEvent(
                "Cours Base de données",
                referenceNow.plus(90, ChronoUnit.MINUTES),
                referenceNow.plus(180, ChronoUnit.MINUTES),
                "Mme Martin"
        );

        RoomEvent labUpcoming = new RoomEvent(
                "TP DevOps",
                referenceNow.plus(20, ChronoUnit.MINUTES),
                referenceNow.plus(120, ChronoUnit.MINUTES),
                "M. Bernard"
        );

        RoomEvent labAvailableLater = new RoomEvent(
                "TP Java",
                referenceNow.plus(120, ChronoUnit.MINUTES),
                referenceNow.plus(210, ChronoUnit.MINUTES),
                "Mme Durand"
        );

        return List.of(
                new Room(
                        "AMPHI-A",
                        "Amphithéâtre A",
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
                        buildingB,
                        "RDC",
                        12,
                        "Réunion",
                        true,
                        List.of()
                )
        );
    }
}