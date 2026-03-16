package com.example.roomstatus.integration.google;

import com.example.roomstatus.config.AppProperties;
import com.example.roomstatus.exception.GoogleIntegrationException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
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
    private final HttpTransport httpTransport;
    private final JsonFactory jsonFactory;

    public GoogleAdminDirectoryClient(
            Directory directoryService,
            AppProperties properties,
            HttpTransport httpTransport,
            JsonFactory jsonFactory
    ) {
        this.directoryService = directoryService;
        this.properties = properties;
        this.httpTransport = httpTransport;
        this.jsonFactory = jsonFactory;
    }

    public List<com.google.api.services.admin.directory.model.Building> listBuildings() {
        return listBuildings(directoryService);
    }

    public List<com.google.api.services.admin.directory.model.Building> listBuildings(String accessToken) {
        return listBuildings(buildDirectoryService(accessToken));
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
            throw new GoogleIntegrationException(
                    "Impossible de recuperer les ressources calendrier Google Workspace%s"
                            .formatted(describeGoogleError(exception)),
                    exception
            );
        }
    }

    private List<com.google.api.services.admin.directory.model.Building> listBuildings(Directory client) {
        List<com.google.api.services.admin.directory.model.Building> buildings = new ArrayList<>();
        String pageToken = null;

        try {
            do {
                var request = client.resources().buildings().list(properties.getCustomer());
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
            throw new GoogleIntegrationException(
                    "Impossible de recuperer les batiments Google Workspace%s".formatted(describeGoogleError(exception)),
                    exception
            );
        }
    }

    private Directory buildDirectoryService(String accessToken) {
        HttpRequestInitializer requestInitializer = request -> {
            request.getHeaders().setAuthorization("Bearer " + accessToken);
            request.setConnectTimeout(properties.getConnectTimeoutMs());
            request.setReadTimeout(properties.getReadTimeoutMs());
        };

        return new Directory.Builder(httpTransport, jsonFactory, requestInitializer)
                .setApplicationName(properties.getApplicationName())
                .build();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String describeGoogleError(IOException exception) {
        if (exception instanceof GoogleJsonResponseException googleException
                && googleException.getDetails() != null
                && googleException.getDetails().getMessage() != null) {
            return " (%s)".formatted(googleException.getDetails().getMessage());
        }

        return "";
    }
}
