package com.example.roomstatus.integration.google;

import com.example.roomstatus.config.AppProperties;
import com.example.roomstatus.exception.GoogleIntegrationException;
import com.example.roomstatus.model.RoomEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "true")
public class GoogleCalendarClient {

    private final RestClient restClient;
    private final GoogleOAuthTokenService googleOAuthTokenService;
    private final AppProperties properties;

    public GoogleCalendarClient(GoogleOAuthTokenService googleOAuthTokenService, AppProperties properties) {
        this.restClient = RestClient.builder()
                .baseUrl("https://www.googleapis.com/calendar/v3")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.googleOAuthTokenService = googleOAuthTokenService;
        this.properties = properties;
    }

    public boolean canAccessCalendarApi() {
        try {
            listAccessibleCalendars();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public List<String> listAccessibleCalendars() {
        String accessToken = googleOAuthTokenService.getAccessTokenValue();

        try {
            CalendarListResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users/me/calendarList")
                            .queryParam("maxResults", 250)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(CalendarListResponse.class);

            if (response == null || response.items == null) {
                return List.of();
            }

            return response.items.stream()
                    .map(item -> item.id)
                    .filter(id -> id != null && !id.isBlank())
                    .toList();

        } catch (RestClientResponseException exception) {
            throw new GoogleIntegrationException(
                    "Impossible de lire la liste des calendriers accessibles (%s)".formatted(exception.getStatusCode()),
                    exception
            );
        }
    }

    public List<RoomEvent> listEvents(String calendarId, LocalDate start, LocalDate end) {
        String accessToken = googleOAuthTokenService.getAccessTokenValue();

        ZoneId zoneId = ZoneId.of(properties.getTimeZone());
        Instant timeMin = start.atStartOfDay(zoneId).toInstant();
        Instant timeMax = end.plusDays(1).atStartOfDay(zoneId).toInstant();

        try {
            EventsResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/calendars/{calendarId}/events")
                            .queryParam("singleEvents", true)
                            .queryParam("orderBy", "startTime")
                            .queryParam("showDeleted", false)
                            .queryParam("timeMin", timeMin.toString())
                            .queryParam("timeMax", timeMax.toString())
                            .queryParam("timeZone", properties.getTimeZone())
                            .build(calendarId))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(EventsResponse.class);

            if (response == null || response.items == null) {
                return List.of();
            }

            List<RoomEvent> events = new ArrayList<>();

            for (GoogleEvent item : response.items) {
                RoomEvent roomEvent = toRoomEvent(item, zoneId);
                if (roomEvent != null) {
                    events.add(roomEvent);
                }
            }

            return events.stream()
                    .sorted(Comparator.comparing(RoomEvent::start))
                    .toList();

        } catch (RestClientResponseException exception) {
            throw new GoogleIntegrationException(
                    "Impossible de lire les événements du calendrier '%s' (%s). Vérifie que ce calendrier est bien partagé avec ton compte."
                            .formatted(calendarId, exception.getStatusCode()),
                    exception
            );
        }
    }

    private RoomEvent toRoomEvent(GoogleEvent event, ZoneId zoneId) {
        if (event == null || event.start == null || event.end == null) {
            return null;
        }

        Instant start = toInstant(event.start, zoneId);
        Instant end = toInstant(event.end, zoneId);

        if (start == null || end == null) {
            return null;
        }

        String organizer = null;
        if (event.organizer != null) {
            organizer = firstNonBlank(event.organizer.displayName, event.organizer.email);
        }
        if (organizer == null && event.creator != null) {
            organizer = firstNonBlank(event.creator.displayName, event.creator.email);
        }

        return new RoomEvent(
                firstNonBlank(event.summary, "Événement sans titre"),
                start,
                end,
                organizer
        );
    }

    private Instant toInstant(EventDateTime dateTime, ZoneId zoneId) {
        if (dateTime.dateTime != null && !dateTime.dateTime.isBlank()) {
            return Instant.parse(dateTime.dateTime);
        }
        if (dateTime.date != null && !dateTime.date.isBlank()) {
            return LocalDate.parse(dateTime.date).atStartOfDay(zoneId).toInstant();
        }
        return null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return null;
    }

    private static class CalendarListResponse {
        public List<CalendarListItem> items;
    }

    private static class CalendarListItem {
        public String id;
        public String summary;
    }

    private static class EventsResponse {
        public List<GoogleEvent> items;
    }

    private static class GoogleEvent {
        public String summary;
        public EventDateTime start;
        public EventDateTime end;
        public Person organizer;
        public Person creator;
    }

    private static class EventDateTime {
        public String dateTime;
        public String date;
    }

    private static class Person {
        public String displayName;
        public String email;
    }
}