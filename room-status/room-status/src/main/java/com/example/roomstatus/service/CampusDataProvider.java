package com.example.roomstatus.service;

import com.example.roomstatus.model.Building;
import com.example.roomstatus.model.Room;
import com.example.roomstatus.model.RoomEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface CampusDataProvider {

    List<Building> getBuildings();

    List<Room> getRooms();

    Room getRoomByCode(String code);

    List<RoomEvent> getRoomSchedule(String code, LocalDate start, LocalDate end);

    default Instant getLastSuccessfulSync() {
        return null;
    }

    default boolean usesGoogle() {
        return false;
    }
}