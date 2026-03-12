package com.example.roomstatus.service;

import com.example.roomstatus.dto.common.BuildingDto;
import com.example.roomstatus.mapper.BuildingMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingService {

    private final CampusDataProvider campusDataProvider;
    private final BuildingMapper buildingMapper;

    public BuildingService(CampusDataProvider campusDataProvider, BuildingMapper buildingMapper) {
        this.campusDataProvider = campusDataProvider;
        this.buildingMapper = buildingMapper;
    }

    @Cacheable("buildings")
    public List<BuildingDto> getBuildings() {
        return campusDataProvider.getBuildings().stream()
                .map(buildingMapper::toDto)
                .toList();
    }
}