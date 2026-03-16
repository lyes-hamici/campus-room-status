package com.example.roomstatus.service;

import com.example.roomstatus.dto.common.BuildingDto;
import com.example.roomstatus.integration.google.GoogleAdminDirectoryClient;
import com.example.roomstatus.integration.google.GoogleMapper;
import com.example.roomstatus.mapper.BuildingMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingService {

    private final CampusDataProvider campusDataProvider;
    private final GoogleAdminDirectoryClient googleAdminDirectoryClient;
    private final GoogleMapper googleMapper;
    private final BuildingMapper buildingMapper;

    public BuildingService(
            CampusDataProvider campusDataProvider,
            GoogleAdminDirectoryClient googleAdminDirectoryClient,
            GoogleMapper googleMapper,
            BuildingMapper buildingMapper
    ) {
        this.campusDataProvider = campusDataProvider;
        this.googleAdminDirectoryClient = googleAdminDirectoryClient;
        this.googleMapper = googleMapper;
        this.buildingMapper = buildingMapper;
    }

    @Cacheable("buildings")
    public List<BuildingDto> getBuildings() {
        return campusDataProvider.getBuildings().stream()
                .map(buildingMapper::toDto)
                .toList();
    }

    @Cacheable(value = "buildings", key = "#accessToken")
    public List<BuildingDto> getBuildings(String accessToken) {
        return googleAdminDirectoryClient.listBuildings(accessToken).stream()
                .map(googleMapper::toBuilding)
                .map(buildingMapper::toDto)
                .toList();
    }
}
