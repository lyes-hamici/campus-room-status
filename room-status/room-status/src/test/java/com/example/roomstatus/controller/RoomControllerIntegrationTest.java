package com.example.roomstatus.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn404WhenRoomDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/UNKNOWN-ROOM"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("ROOM_NOT_FOUND"));
    }

    @Test
    void shouldReturn400WhenCapacityRangeIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/rooms")
                        .param("capacity_min", "50")
                        .param("capacity_max", "20"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    void shouldReturn400WhenOrderIsProvidedWithoutSort() throws Exception {
        mockMvc.perform(get("/api/v1/rooms")
                        .param("order", "desc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.error.message").value(containsString("order")));
    }

    @Test
    void shouldReturn400WhenScheduleStartIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/AMPHI-A/schedule")
                        .param("end", "2030-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.error.message").value(containsString("start")));
    }

    @Test
    void shouldReturn400WhenScheduleDateFormatIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/AMPHI-A/schedule")
                        .param("start", "01-01-2030")
                        .param("end", "2030-01-02"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    void shouldIgnoreBlankOptionalFilters() throws Exception {
        mockMvc.perform(get("/api/v1/rooms")
                        .param("status", "   ")
                        .param("sort", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rooms").isArray());
    }

    @Test
    void shouldReturn200WhenListingRooms() throws Exception {
        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.count").isNumber());
    }
}