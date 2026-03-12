package com.example.roomstatus.integration.google;

import com.example.roomstatus.config.AppProperties;
import com.google.api.client.util.DateTime;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.calendar.Calendar;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class GoogleConnectionChecker {

    private final AppProperties properties;
    private final ObjectProvider<Directory> directoryProvider;
    private final ObjectProvider<Calendar> calendarProvider;

    public GoogleConnectionChecker(
            AppProperties properties,
            ObjectProvider<Directory> directoryProvider,
            ObjectProvider<Calendar> calendarProvider
    ) {
        this.properties = properties;
        this.directoryProvider = directoryProvider;
        this.calendarProvider = calendarProvider;
    }

    public boolean isAdminDirectoryConnected() {
        if (!properties.isEnabled()) {
            return false;
        }

        Directory directoryService = directoryProvider.getIfAvailable();
        if (directoryService == null) {
            return false;
        }

        try {
            directoryService.resources()
                    .calendars()
                    .list(properties.getCustomer())
                    .setMaxResults(1)
                    .execute();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean isCalendarConnected() {
        if (!properties.isEnabled()) {
            return false;
        }

        Calendar calendarService = calendarProvider.getIfAvailable();
        if (calendarService == null) {
            return false;
        }

        try {
            long now = System.currentTimeMillis();

            calendarService.events()
                    .list("primary")
                    .setMaxResults(1)
                    .setSingleEvents(true)
                    .setTimeMin(new DateTime(now))
                    .setTimeMax(new DateTime(now + 3_600_000L))
                    .execute();

            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}