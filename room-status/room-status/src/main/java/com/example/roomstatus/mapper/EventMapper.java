package com.example.roomstatus.mapper;

import com.example.roomstatus.dto.common.EventDto;
import com.example.roomstatus.model.RoomEvent;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventDto toDto(RoomEvent event) {
        return new EventDto(
                event.title(),
                event.start(),
                event.end(),
                event.organizer()
        );
    }
}