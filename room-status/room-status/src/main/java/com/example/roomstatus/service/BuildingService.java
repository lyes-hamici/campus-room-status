package com.example.roomstatus.service;

import com.example.roomstatus.dto.common.BuildingDto;
import com.example.roomstatus.mapper.BuildingMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingService {

    private final MockCampusDataService mockCampusDataService;
    private final BuildingMapper buildingMapper;

    public BuildingService(MockCampusDataService mockCampusDataService, BuildingMapper buildingMapper) {
        this.mockCampusDataService = mockCampusDataService;
        this.buildingMapper = buildingMapper;
    }

    @Cacheable("buildings")
    public List<BuildingDto> getBuildings() {
        return mockCampusDataService.getBuildings().stream()
                .map(buildingMapper::toDto)
                .toList();
    }
}