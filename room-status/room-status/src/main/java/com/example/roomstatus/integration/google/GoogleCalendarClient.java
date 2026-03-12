package com.example.roomstatus.integration.google;

import com.example.roomstatus.config.AppProperties;
import com.example.roomstatus.exception.GoogleIntegrationException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "true")
public class GoogleCalendarClient {

    private final Calendar calendarService;
    private final AppProperties properties;

    public GoogleCalendarClient(Calendar calendarService, AppProperties properties) {
        this.calendarService = calendarService;
        this.properties = properties;
    }

    public List<Event> listEvents(String calendarId, LocalDate start, LocalDate end) {
        List<Event> events = new ArrayList<>();
        String pageToken = null;

        ZoneId zoneId = ZoneId.of(properties.getTimeZone());
        DateTime timeMin = new DateTime(start.atStartOfDay(zoneId).toInstant().toEpochMilli());
        DateTime timeMax = new DateTime(end.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli());

        try {
            do {
                var request = calendarService.events().list(calendarId);
                request.setSingleEvents(true);
                request.setOrderBy("startTime");
                request.setShowDeleted(false);
                request.setTimeMin(timeMin);
                request.setTimeMax(timeMax);
                request.setTimeZone(properties.getTimeZone());
                request.setMaxResults(properties.getMaxResultsPerPage());

                if (hasText(pageToken)) {
                    request.setPageToken(pageToken);
                }

                var response = request.execute();

                if (response.getItems() != null) {
                    events.addAll(
                            response.getItems().stream()
                                    .filter(event -> !"cancelled".equalsIgnoreCase(event.getStatus()))
                                    .toList()
                    );
                }

                pageToken = response.getNextPageToken();
            } while (hasText(pageToken));

            return events;
        } catch (IOException exception) {
            throw new GoogleIntegrationException(
                    "Impossible de récupérer les événements Google Calendar pour le calendrier '%s'".formatted(calendarId),
                    exception
            );
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}