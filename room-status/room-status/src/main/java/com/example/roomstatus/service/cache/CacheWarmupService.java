package com.example.roomstatus.service.cache;

import com.example.roomstatus.dto.request.RoomSearchRequest;
import com.example.roomstatus.service.BuildingService;
import com.example.roomstatus.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class CacheWarmupService {

    private static final Logger log = LoggerFactory.getLogger(CacheWarmupService.class);

    private final BuildingService buildingService;
    private final RoomService roomService;

    public CacheWarmupService(BuildingService buildingService, RoomService roomService) {
        this.buildingService = buildingService;
        this.roomService = roomService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmUpCaches() {
        try {
            buildingService.getBuildings();
            roomService.getRooms(RoomSearchRequest.empty());
        } catch (Exception exception) {
            log.warn("Le préchauffage du cache a échoué : {}", exception.getMessage());
        }
    }
}