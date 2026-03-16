package com.example.roomstatus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.campus")
public class CampusCatalogProperties {

    private List<RoomDefinition> rooms = new ArrayList<>();

    public List<RoomDefinition> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomDefinition> rooms) {
        this.rooms = rooms;
    }

    public static class RoomDefinition {
        private String code;
        private String name;
        private String calendarId;
        private String buildingId;
        private String buildingName;
        private String address;
        private String floor;
        private int capacity;
        private String type;
        private boolean maintenance;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCalendarId() {
            return calendarId;
        }

        public void setCalendarId(String calendarId) {
            this.calendarId = calendarId;
        }

        public String getBuildingId() {
            return buildingId;
        }

        public void setBuildingId(String buildingId) {
            this.buildingId = buildingId;
        }

        public String getBuildingName() {
            return buildingName;
        }

        public void setBuildingName(String buildingName) {
            this.buildingName = buildingName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getFloor() {
            return floor;
        }

        public void setFloor(String floor) {
            this.floor = floor;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isMaintenance() {
            return maintenance;
        }

        public void setMaintenance(boolean maintenance) {
            this.maintenance = maintenance;
        }
    }
}