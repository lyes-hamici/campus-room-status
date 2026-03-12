package com.example.roomstatus.service;

import com.example.roomstatus.exception.RoomNotFoundException;
import com.example.roomstatus.integration.google.GoogleAdminDirectoryClient;
import com.example.roomstatus.integration.google.GoogleCalendarClient;
import com.example.roomstatus.integration.google.GoogleMapper;
import com.example.roomstatus.model.Building;
import com.example.roomstatus.model.Room;
import com.example.roomstatus.model.RoomEvent;
import com.google.api.services.admin.directory.model.CalendarResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "true")
public class GoogleWorkspaceDataService implements CampusDataProvider {

    private final GoogleAdminDirectoryClient googleAdminDirectoryClient;
    private final GoogleCalendarClient googleCalendarClient;
    private final GoogleMapper googleMapper;
    private final com.example.roomstatus.config.AppProperties properties;
    private final AtomicReference<Instant> lastSuccessfulSync = new AtomicReference<>();

    public GoogleWorkspaceDataService(
            GoogleAdminDirectoryClient googleAdminDirectoryClient,
            GoogleCalendarClient googleCalendarClient,
            GoogleMapper googleMapper,
            com.example.roomstatus.config.AppProperties properties
    ) {
        this.googleAdminDirectoryClient = googleAdminDirectoryClient;
        this.googleCalendarClient = googleCalendarClient;
        this.googleMapper = googleMapper;
        this.properties = properties;
    }

    @Override
    public List<Building> getBuildings() {
        DirectoryIndex directoryIndex = loadDirectoryIndex();
        markSuccessfulSync();
        return List.copyOf(directoryIndex.buildingsById().values());
    }

    @Override
    public List<Room> getRooms() {
        DirectoryIndex directoryIndex = loadDirectoryIndex();
        LocalDate today = LocalDate.now(ZoneId.of(properties.getTimeZone()));

        List<Room> rooms = directoryIndex.calendarResources().stream()
                .map(calendarResource -> googleMapper.toRoom(
                        calendarResource,
                        googleMapper.resolveBuilding(calendarResource, directoryIndex.buildingsById()),
                        googleCalendarClient.listEvents(calendarResource.getResourceEmail(), today, today)
                ))
                .toList();

        markSuccessfulSync();
        return rooms;
    }

    @Override
    public Room getRoomByCode(String code) {
        String normalizedCode = normalizeCode(code);
        DirectoryIndex directoryIndex = loadDirectoryIndex();
        CalendarResource calendarResource = findCalendarResourceByCode(normalizedCode, directoryIndex.calendarResources());
        LocalDate today = LocalDate.now(ZoneId.of(properties.getTimeZone()));

        Room room = googleMapper.toRoom(
                calendarResource,
                googleMapper.resolveBuilding(calendarResource, directoryIndex.buildingsById()),
                googleCalendarClient.listEvents(calendarResource.getResourceEmail(), today, today)
        );

        markSuccessfulSync();
        return room;
    }

    @Override
    public List<RoomEvent> getRoomSchedule(String code, LocalDate start, LocalDate end) {
        String normalizedCode = normalizeCode(code);
        DirectoryIndex directoryIndex = loadDirectoryIndex();
        CalendarResource calendarResource = findCalendarResourceByCode(normalizedCode, directoryIndex.calendarResources());

        List<RoomEvent> events = googleMapper.toRoomEvents(
                googleCalendarClient.listEvents(calendarResource.getResourceEmail(), start, end)
        );

        markSuccessfulSync();
        return events;
    }

    @Override
    public Instant getLastSuccessfulSync() {
        return lastSuccessfulSync.get();
    }

    @Override
    public boolean usesGoogle() {
        return true;
    }

    private DirectoryIndex loadDirectoryIndex() {
        List<com.google.api.services.admin.directory.model.Building> googleBuildings =
                googleAdminDirectoryClient.listBuildings();

        List<CalendarResource> calendarResources =
                googleAdminDirectoryClient.listCalendarResources();

        Map<String, Building> buildingsById = googleMapper.toBuildingIndex(googleBuildings);

        for (CalendarResource calendarResource : calendarResources) {
            googleMapper.resolveBuilding(calendarResource, buildingsById);
        }

        return new DirectoryIndex(buildingsById, calendarResources);
    }

    private CalendarResource findCalendarResourceByCode(String code, List<CalendarResource> resources) {
        return resources.stream()
                .filter(resource -> googleMapper.resolveRoomCode(resource).equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new RoomNotFoundException(
                        "La salle avec le code '%s' n'existe pas".formatted(code)
                ));
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim();
    }

    private void markSuccessfulSync() {
        lastSuccessfulSync.set(Instant.now());
    }

    private record DirectoryIndex(
            Map<String, Building> buildingsById,
            List<CalendarResource> calendarResources
    ) {
    }
}