package com.example.roomstatus.service;

import com.example.roomstatus.model.Building;
import com.example.roomstatus.model.Room;
import com.example.roomstatus.model.RoomEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Point d'entree unique pour recuperer les donnees campus.
 * L'application s'appuie sur cette interface pour rester independante
 * de la source reelle: donnees mockees en local ou Google Workspace.
 */
public interface CampusDataProvider {

    List<Building> getBuildings();

    List<Room> getRooms();

    Room getRoomByCode(String code);

    List<RoomEvent> getRoomSchedule(String code, LocalDate start, LocalDate end);

    /**
     * Renseigne la date de la derniere synchronisation reussie quand
     * l'implementation parle a une source externe.
     */
    default Instant getLastSuccessfulSync() {
        return null;
    }

    /**
     * Permet d'exposer simplement au reste de l'application si la source
     * active est Google Workspace ou le mode mock.
     */
    default boolean usesGoogle() {
        return false;
    }
}
