package com.example.roomstatus.service;

import com.example.roomstatus.dto.common.FilterDto;
import com.example.roomstatus.dto.common.RoomDto;
import com.example.roomstatus.dto.common.RoomSummaryDto;
import com.example.roomstatus.dto.request.RoomSearchRequest;
import com.example.roomstatus.dto.response.RoomScheduleResponse;
import com.example.roomstatus.exception.InvalidRequestException;
import com.example.roomstatus.mapper.RoomMapper;
import com.example.roomstatus.model.Room;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class RoomService {

    private static final Set<String> ALLOWED_STATUSES = Set.of("available", "occupied", "upcoming", "maintenance");
    private static final Set<String> ALLOWED_SORTS = Set.of("name", "capacity", "status");
    private static final Set<String> ALLOWED_ORDERS = Set.of("asc", "desc");

    private final CampusDataProvider campusDataProvider;
    private final RoomMapper roomMapper;
    private final AvailabilityService availabilityService;

    public RoomService(
            CampusDataProvider campusDataProvider,
            RoomMapper roomMapper,
            AvailabilityService availabilityService
    ) {
        this.campusDataProvider = campusDataProvider;
        this.roomMapper = roomMapper;
        this.availabilityService = availabilityService;
    }

    @Cacheable("rooms")
    public List<RoomSummaryDto> getRooms(RoomSearchRequest request) {
        RoomSearchRequest normalizedRequest = normalizeRequest(request);
        validateSearchRequest(normalizedRequest);

        Instant referenceTime = Instant.now();

        return campusDataProvider.getRooms().stream()
                .filter(room -> matchesFilters(room, normalizedRequest, referenceTime))
                .sorted(buildComparator(normalizedRequest, referenceTime))
                .map(room -> roomMapper.toSummaryDto(room, referenceTime))
                .toList();
    }

    @Cacheable(value = "room-details", key = "#code")
    public RoomDto getRoomByCode(String code) {
        Room room = campusDataProvider.getRoomByCode(normalizeRequiredText(code, "code"));
        return roomMapper.toDetailDto(room, Instant.now());
    }

    @Cacheable(value = "room-schedules", key = "#code + ':' + #start + ':' + #end")
    public RoomScheduleResponse getRoomSchedule(String code, LocalDate start, LocalDate end) {
        String normalizedCode = normalizeRequiredText(code, "code");

        if (start == null || end == null) {
            throw new InvalidRequestException("Les paramètres 'start' et 'end' sont obligatoires");
        }

        if (start.isAfter(end)) {
            throw new InvalidRequestException("La date de début doit être antérieure ou égale à la date de fin");
        }

        return new RoomScheduleResponse(
                normalizedCode,
                new RoomScheduleResponse.Period(start, end),
                roomMapper.toEventDtos(campusDataProvider.getRoomSchedule(normalizedCode, start, end))
        );
    }

    public FilterDto toFilterDto(RoomSearchRequest request) {
        RoomSearchRequest normalizedRequest = normalizeRequest(request);

        return new FilterDto(
                normalizedRequest.building(),
                normalizedRequest.type(),
                normalizedRequest.status(),
                normalizedRequest.capacityMin(),
                normalizedRequest.capacityMax(),
                normalizedRequest.sort(),
                normalizedRequest.order()
        );
    }

    private void validateSearchRequest(RoomSearchRequest request) {
        if (request.capacityMin() != null && request.capacityMin() < 0) {
            throw new InvalidRequestException("capacity_min doit être supérieur ou égal à 0");
        }

        if (request.capacityMax() != null && request.capacityMax() < 0) {
            throw new InvalidRequestException("capacity_max doit être supérieur ou égal à 0");
        }

        if (request.capacityMin() != null && request.capacityMax() != null
                && request.capacityMin() > request.capacityMax()) {
            throw new InvalidRequestException("capacity_min ne peut pas être supérieur à capacity_max");
        }

        if (request.order() != null && request.sort() == null) {
            throw new InvalidRequestException("order ne peut pas être utilisé sans sort");
        }

        if (request.status() != null && !ALLOWED_STATUSES.contains(request.status().toLowerCase(Locale.ROOT))) {
            throw new InvalidRequestException(
                    "status doit être l'une des valeurs suivantes : available, occupied, upcoming, maintenance"
            );
        }

        if (request.sort() != null && !ALLOWED_SORTS.contains(request.sort().toLowerCase(Locale.ROOT))) {
            throw new InvalidRequestException(
                    "sort doit être l'une des valeurs suivantes : name, capacity, status"
            );
        }

        if (request.order() != null && !ALLOWED_ORDERS.contains(request.order().toLowerCase(Locale.ROOT))) {
            throw new InvalidRequestException("order doit être asc ou desc");
        }
    }

    private boolean matchesFilters(Room room, RoomSearchRequest request, Instant referenceTime) {
        String resolvedStatus = availabilityService.resolveStatusLabel(room, referenceTime);

        return streamOf(
                request.building() == null || room.building().id().equalsIgnoreCase(request.building()),
                request.type() == null || room.type().equalsIgnoreCase(request.type()),
                request.status() == null || resolvedStatus.equalsIgnoreCase(request.status()),
                request.capacityMin() == null || room.capacity() >= request.capacityMin(),
                request.capacityMax() == null || room.capacity() <= request.capacityMax()
        ).allMatch(Boolean::booleanValue);
    }

    private Comparator<Room> buildComparator(RoomSearchRequest request, Instant referenceTime) {
        String sort = request.sort() == null ? "name" : request.sort().toLowerCase(Locale.ROOT);
        String order = request.order() == null ? "asc" : request.order().toLowerCase(Locale.ROOT);

        Comparator<Room> comparator = switch (sort) {
            case "capacity" -> Comparator.comparingInt(Room::capacity);
            case "status" -> Comparator.comparing(room -> availabilityService.resolveStatusLabel(room, referenceTime));
            default -> Comparator.comparing(Room::name, String.CASE_INSENSITIVE_ORDER);
        };

        return "desc".equals(order) ? comparator.reversed() : comparator;
    }

    private RoomSearchRequest normalizeRequest(RoomSearchRequest request) {
        if (request == null) {
            return RoomSearchRequest.empty();
        }
        return request.normalized();
    }

    private String normalizeRequiredText(String value, String fieldName) {
        if (value == null) {
            throw new InvalidRequestException("Le paramètre '%s' est obligatoire".formatted(fieldName));
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidRequestException("Le paramètre '%s' est obligatoire".formatted(fieldName));
        }

        return trimmed;
    }

    @SafeVarargs
    private static <T> Stream<T> streamOf(T... items) {
        return Stream.of(items);
    }
}