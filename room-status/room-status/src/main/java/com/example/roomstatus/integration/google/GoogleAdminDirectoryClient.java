package com.example.roomstatus.integration.google;

import com.example.roomstatus.config.AppProperties;
import com.example.roomstatus.exception.GoogleIntegrationException;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.CalendarResource;
import com.google.api.services.admin.directory.model.CalendarResources;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "true")
public class GoogleAdminDirectoryClient {

    private final Directory directoryService;
    private final AppProperties properties;

    public GoogleAdminDirectoryClient(Directory directoryService, AppProperties properties) {
        this.directoryService = directoryService;
        this.properties = properties;
    }

    public List<com.google.api.services.admin.directory.model.Building> listBuildings() {
        List<com.google.api.services.admin.directory.model.Building> buildings = new ArrayList<>();
        String pageToken = null;

        try {
            do {
                var request = directoryService.resources().buildings().list(properties.getCustomer());
                request.setMaxResults(properties.getMaxResultsPerPage());

                if (hasText(pageToken)) {
                    request.setPageToken(pageToken);
                }

                var response = request.execute();

                if (response.getBuildings() != null) {
                    buildings.addAll(response.getBuildings());
                }

                pageToken = response.getNextPageToken();
            } while (hasText(pageToken));

            return buildings;
        } catch (IOException exception) {
            throw new GoogleIntegrationException("Impossible de récupérer les bâtiments Google Workspace", exception);
        }
    }

    public List<CalendarResource> listCalendarResources() {
        List<CalendarResource> resources = new ArrayList<>();
        String pageToken = null;

        try {
            do {
                var request = directoryService.resources().calendars().list(properties.getCustomer());
                request.setMaxResults(properties.getMaxResultsPerPage());

                if (hasText(pageToken)) {
                    request.setPageToken(pageToken);
                }

                CalendarResources response = request.execute();

                if (response.getItems() != null) {
                    resources.addAll(response.getItems());
                }

                pageToken = response.getNextPageToken();
            } while (hasText(pageToken));

            return resources;
        } catch (IOException exception) {
            throw new GoogleIntegrationException("Impossible de récupérer les ressources calendrier Google Workspace", exception);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}