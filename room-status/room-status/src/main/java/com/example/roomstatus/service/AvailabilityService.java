package com.example.roomstatus.service;

import com.example.roomstatus.model.Room;
import com.example.roomstatus.model.RoomEvent;
import com.example.roomstatus.model.RoomStatus;
import com.example.roomstatus.util.DateTimeUtils;
import com.example.roomstatus.util.StatusResolver;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

/**
 * Calcule l'etat courant d'une salle a partir de son planning du jour.
 * Toute la logique de "available / occupied / upcoming / maintenance"
 * est centralisee ici pour rester coherente partout dans l'API.
 */
@Service
public class AvailabilityService {

    public Optional<RoomEvent> findCurrentEvent(Room room, Instant referenceTime) {
        return room.scheduleToday().stream()
                .filter(event -> DateTimeUtils.isOngoing(event, referenceTime))
                .min(Comparator.comparing(RoomEvent::start));
    }

    public Optional<RoomEvent> findNextEvent(Room room, Instant referenceTime) {
        return room.scheduleToday().stream()
                .filter(event -> event.start().isAfter(referenceTime))
                .min(Comparator.comparing(RoomEvent::start));
    }

    public RoomStatus resolveStatus(Room room, Instant referenceTime) {
        // Le resolveur travaille a partir de l'evenement en cours,
        // du prochain evenement et du flag de maintenance.
        return StatusResolver.resolve(
                room.maintenance(),
                findCurrentEvent(room, referenceTime),
                findNextEvent(room, referenceTime),
                referenceTime
        );
    }

    public String resolveStatusLabel(Room room, Instant referenceTime) {
        return resolveStatus(room, referenceTime).name().toLowerCase(Locale.ROOT);
    }
}
