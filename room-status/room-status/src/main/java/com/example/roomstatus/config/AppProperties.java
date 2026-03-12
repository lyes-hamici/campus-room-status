package com.example.roomstatus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.google")
public class AppProperties {

    private boolean enabled = false;
    private String applicationName = "room-status";
    private String customer = "my_customer";
    private String delegatedUser;
    private String serviceAccountKeyPath;
    private String timeZone = "Europe/Paris";
    private int connectTimeoutMs = 5000;
    private int readTimeoutMs = 10000;
    private int maxResultsPerPage = 100;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getDelegatedUser() {
        return delegatedUser;
    }

    public void setDelegatedUser(String delegatedUser) {
        this.delegatedUser = delegatedUser;
    }

    public String getServiceAccountKeyPath() {
        return serviceAccountKeyPath;
    }

    public void setServiceAccountKeyPath(String serviceAccountKeyPath) {
        this.serviceAccountKeyPath = serviceAccountKeyPath;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    public int getMaxResultsPerPage() {
        return maxResultsPerPage;
    }

    public void setMaxResultsPerPage(int maxResultsPerPage) {
        this.maxResultsPerPage = maxResultsPerPage;
    }
}