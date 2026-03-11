package com.example.roomstatus.mapper;

import com.example.roomstatus.dto.common.EventDto;
import com.example.roomstatus.dto.common.RoomDto;
import com.example.roomstatus.dto.common.RoomSummaryDto;
import com.example.roomstatus.model.Room;
import com.example.roomstatus.model.RoomEvent;
import com.example.roomstatus.service.AvailabilityService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class RoomMapper {

    private final BuildingMapper buildingMapper;
    private final EventMapper eventMapper;
    private final AvailabilityService availabilityService;

    public RoomMapper(BuildingMapper buildingMapper, EventMapper eventMapper, AvailabilityService availabilityService) {
        this.buildingMapper = buildingMapper;
        this.eventMapper = eventMapper;
        this.availabilityService = availabilityService;
    }

    public RoomSummaryDto toSummaryDto(Room room, Instant referenceTime) {
        EventDto currentEvent = availabilityService.findCurrentEvent(room, referenceTime)
                .map(eventMapper::toDto)
                .orElse(null);

        EventDto nextEvent = availabilityService.findNextEvent(room, referenceTime)
                .map(eventMapper::toDto)
                .orElse(null);

        return new RoomSummaryDto(
                room.code(),
                room.name(),
                buildingMapper.toDto(room.building()),
                room.floor(),
                room.capacity(),
                room.type(),
                availabilityService.resolveStatusLabel(room, referenceTime),
                currentEvent,
                nextEvent
        );
    }

    public RoomDto toDetailDto(Room room, Instant referenceTime) {
        EventDto currentEvent = availabilityService.findCurrentEvent(room, referenceTime)
                .map(eventMapper::toDto)
                .orElse(null);

        EventDto nextEvent = availabilityService.findNextEvent(room, referenceTime)
                .map(eventMapper::toDto)
                .orElse(null);

        List<EventDto> scheduleToday = room.scheduleToday().stream()
                .map(eventMapper::toDto)
                .toList();

        return new RoomDto(
                room.code(),
                room.name(),
                buildingMapper.toDto(room.building()),
                room.floor(),
                room.capacity(),
                room.type(),
                availabilityService.resolveStatusLabel(room, referenceTime),
                currentEvent,
                nextEvent,
                scheduleToday
        );
    }

    public List<EventDto> toEventDtos(List<RoomEvent> events) {
        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }
}