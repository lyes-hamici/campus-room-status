package com.example.roomstatus.integration.google;

import com.example.roomstatus.model.Building;
import com.example.roomstatus.model.Room;
import com.example.roomstatus.model.RoomEvent;
import com.google.api.services.admin.directory.model.BuildingAddress;
import com.google.api.services.admin.directory.model.CalendarResource;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GoogleMapper {

    public Map<String, Building> toBuildingIndex(List<com.google.api.services.admin.directory.model.Building> googleBuildings) {
        Map<String, Building> index = new LinkedHashMap<>();

        for (com.google.api.services.admin.directory.model.Building googleBuilding : googleBuildings) {
            Building building = toBuilding(googleBuilding);
            index.put(building.id(), building);
        }

        return index;
    }

    public Building toBuilding(com.google.api.services.admin.directory.model.Building googleBuilding) {
        List<String> floors = googleBuilding.getFloorNames() == null
                ? List.of()
                : googleBuilding.getFloorNames().stream()
                        .filter(this::hasText)
                        .toList();

        return new Building(
                firstNonBlank(googleBuilding.getBuildingId(), "UNASSIGNED"),
                firstNonBlank(googleBuilding.getBuildingName(), googleBuilding.getDescription(), googleBuilding.getBuildingId(), "Bâtiment"),
                formatAddress(googleBuilding.getAddress()),
                floors
        );
    }

    public Building resolveBuilding(CalendarResource calendarResource, Map<String, Building> buildingsById) {
        String buildingId = blankToNull(calendarResource.getBuildingId());

        if (buildingId == null) {
            return toSyntheticBuilding(calendarResource);
        }

        return buildingsById.computeIfAbsent(buildingId, ignored -> toSyntheticBuilding(calendarResource));
    }

    public Building toSyntheticBuilding(CalendarResource calendarResource) {
        String buildingId = firstNonBlank(calendarResource.getBuildingId(), "UNASSIGNED");
        List<String> floors = hasText(calendarResource.getFloorName())
                ? List.of(calendarResource.getFloorName())
                : List.of();

        return new Building(
                buildingId,
                buildingId.equals("UNASSIGNED") ? "Bâtiment non renseigné" : buildingId,
                "",
                floors
        );
    }

    public Room toRoom(CalendarResource calendarResource, Building building, List<Event> events) {
        return new Room(
                resolveRoomCode(calendarResource),
                firstNonBlank(
                        calendarResource.getResourceName(),
                        calendarResource.getGeneratedResourceName(),
                        resolveLocalPart(calendarResource.getResourceEmail()),
                        "Salle sans nom"
                ),
                firstNonBlank(calendarResource.getResourceEmail(), resolveRoomCode(calendarResource)),
                building,
                defaultString(calendarResource.getFloorName(), ""),
                calendarResource.getCapacity() == null ? 0 : calendarResource.getCapacity(),
                resolveRoomType(calendarResource),
                false,
                toRoomEvents(events)
        );
    }

    public List<RoomEvent> toRoomEvents(List<Event> events) {
        return events.stream()
                .map(this::toRoomEvent)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(RoomEvent::start))
                .toList();
    }

    public String resolveRoomCode(CalendarResource calendarResource) {
        if (hasText(calendarResource.getResourceId())) {
            return calendarResource.getResourceId().trim();
        }

        String emailLocalPart = resolveLocalPart(calendarResource.getResourceEmail());
        if (hasText(emailLocalPart)) {
            return slugify(emailLocalPart);
        }

        return slugify(firstNonBlank(calendarResource.getResourceName(), "unknown-room"));
    }

    private RoomEvent toRoomEvent(Event event) {
        Instant start = toInstant(event.getStart());
        Instant end = toInstant(event.getEnd());

        if (start == null || end == null) {
            return null;
        }

        return new RoomEvent(
                firstNonBlank(event.getSummary(), "Événement sans titre"),
                start,
                end,
                extractOrganizer(event)
        );
    }

    private Instant toInstant(EventDateTime eventDateTime) {
        if (eventDateTime == null) {
            return null;
        }

        if (eventDateTime.getDateTime() != null) {
            return Instant.ofEpochMilli(eventDateTime.getDateTime().getValue());
        }

        if (eventDateTime.getDate() != null) {
            return Instant.ofEpochMilli(eventDateTime.getDate().getValue());
        }

        return null;
    }

    private String extractOrganizer(Event event) {
        if (event.getOrganizer() == null) {
            return null;
        }

        return firstNonBlank(
                event.getOrganizer().getDisplayName(),
                event.getOrganizer().getEmail()
        );
    }

    private String resolveRoomType(CalendarResource calendarResource) {
        return firstNonBlank(
                calendarResource.getResourceType(),
                calendarResource.getResourceCategory(),
                "OTHER"
        );
    }

    private String formatAddress(BuildingAddress address) {
        if (address == null) {
            return "";
        }

        List<String> parts = new ArrayList<>();

        if (address.getAddressLines() != null) {
            parts.addAll(
                    address.getAddressLines().stream()
                            .filter(this::hasText)
                            .toList()
            );
        }

        String postalAndLocality = List.of(
                        blankToNull(address.getPostalCode()),
                        blankToNull(address.getLocality())
                ).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));

        if (hasText(postalAndLocality)) {
            parts.add(postalAndLocality);
        }

        if (hasText(address.getAdministrativeArea())) {
            parts.add(address.getAdministrativeArea());
        }

        if (hasText(address.getRegionCode())) {
            parts.add(address.getRegionCode());
        }

        return String.join(", ", parts);
    }

    private String resolveLocalPart(String email) {
        if (!hasText(email) || !email.contains("@")) {
            return null;
        }

        return email.substring(0, email.indexOf('@'));
    }

    private String slugify(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        normalized = normalized.replaceAll("[^A-Z0-9]+", "-");
        normalized = normalized.replaceAll("^-+", "");
        normalized = normalized.replaceAll("-+$", "");
        return normalized.isEmpty() ? "UNKNOWN" : normalized;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private String blankToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private String defaultString(String value, String defaultValue) {
        return hasText(value) ? value.trim() : defaultValue;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}