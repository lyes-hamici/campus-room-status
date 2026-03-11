package com.example.roomstatus.service.cache;

import com.example.roomstatus.dto.request.RoomSearchRequest;
import com.example.roomstatus.service.BuildingService;
import com.example.roomstatus.service.RoomService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class CacheWarmupService {

    private final BuildingService buildingService;
    private final RoomService roomService;

    public CacheWarmupService(BuildingService buildingService, RoomService roomService) {
        this.buildingService = buildingService;
        this.roomService = roomService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmUpCaches() {
        buildingService.getBuildings();
        roomService.getRooms(RoomSearchRequest.empty());
    }
}