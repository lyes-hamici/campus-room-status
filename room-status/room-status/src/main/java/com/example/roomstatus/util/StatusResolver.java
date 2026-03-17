package com.example.roomstatus.util;

import com.example.roomstatus.model.RoomEvent;
import com.example.roomstatus.model.RoomStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Traduit des informations de planning en un statut metier exploitable par l'API.
 */
public final class StatusResolver {

    private static final Duration UPCOMING_THRESHOLD = Duration.ofMinutes(30);

    private StatusResolver() {
    }

    public static RoomStatus resolve(
            boolean maintenance,
            Optional<RoomEvent> currentEvent,
            Optional<RoomEvent> nextEvent,
            Instant referenceTime
    ) {
        // La maintenance prend toujours le dessus sur le planning.
        if (maintenance) {
            return RoomStatus.MAINTENANCE;
        }
        if (currentEvent.isPresent()) {
            return RoomStatus.OCCUPIED;
        }
        // Une salle est "upcoming" uniquement si le prochain evenement
        // commence bientot selon le seuil defini ci-dessus.
        if (nextEvent.isPresent() && DateTimeUtils.startsWithin(nextEvent.get(), referenceTime, UPCOMING_THRESHOLD)) {
            return RoomStatus.UPCOMING;
        }
        return RoomStatus.AVAILABLE;
    }
}
