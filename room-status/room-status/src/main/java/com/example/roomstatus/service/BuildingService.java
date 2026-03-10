package com.example.roomstatus.service;

import com.example.roomstatus.dto.common.BuildingDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingService {

    public List<BuildingDto> getBuildings() {
        return List.of(
                new BuildingDto(
                        "BAT-A",
                        "Bâtiment A - Informatique",
                        "10 rue de l'Innovation",
                        List.of("RDC", "1", "2")
                ),
                new BuildingDto(
                        "BAT-B",
                        "Bâtiment B - Général",
                        "12 rue de l'Innovation",
                        List.of("RDC", "1")
                )
        );
    }
}