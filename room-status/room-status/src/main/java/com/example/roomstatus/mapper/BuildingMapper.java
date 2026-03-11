package com.example.roomstatus.mapper;

import com.example.roomstatus.dto.common.BuildingDto;
import com.example.roomstatus.model.Building;
import org.springframework.stereotype.Component;

@Component
public class BuildingMapper {

    public BuildingDto toDto(Building building) {
        return new BuildingDto(
                building.id(),
                building.name(),
                building.address(),
                building.floors()
        );
    }
}