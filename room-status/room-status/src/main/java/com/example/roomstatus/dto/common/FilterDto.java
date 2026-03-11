package com.example.roomstatus.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record FilterDto(
        String building,
        String type,
        String status,
        Integer capacityMin,
        Integer capacityMax,
        String sort,
        String order
) {
}