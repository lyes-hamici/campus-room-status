package com.example.roomstatus.util;

import com.example.roomstatus.model.RoomEvent;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Regroupe les comparaisons de dates utilisees dans la logique de disponibilite.
 */
public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static boolean isOngoing(RoomEvent event, Instant referenceTime) {
        return !event.start().isAfter(referenceTime) && event.end().isAfter(referenceTime);
    }

    public static boolean startsWithin(RoomEvent event, Instant referenceTime, Duration duration) {
        Instant upperBound = referenceTime.plus(duration);
        return event.start().isAfter(referenceTime) && !event.start().isAfter(upperBound);
    }

    public static boolean isWithinDateRange(RoomEvent event, LocalDate start, LocalDate end) {
        // Le filtrage par plage de dates repose sur la date UTC de debut de l'evenement.
        LocalDate eventDate = event.start().atZone(ZoneOffset.UTC).toLocalDate();
        return !eventDate.isBefore(start) && !eventDate.isAfter(end);
    }
}
