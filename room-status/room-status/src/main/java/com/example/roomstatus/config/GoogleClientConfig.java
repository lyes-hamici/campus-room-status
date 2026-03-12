package com.example.roomstatus.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.List;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class GoogleClientConfig {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final List<String> GOOGLE_WORKSPACE_SCOPES = List.of(
            "https://www.googleapis.com/auth/admin.directory.resource.calendar.readonly",
            "https://www.googleapis.com/auth/calendar.readonly"
    );

    @Bean
    @ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "true")
    public HttpTransport googleHttpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "true")
    public JsonFactory googleJsonFactory() {
        return JSON_FACTORY;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "true")
    public GoogleCredentials googleCredentials(AppProperties properties) throws IOException {
        validateProperties(properties);

        Path credentialsPath = Path.of(properties.getServiceAccountKeyPath()).toAbsolutePath().normalize();

        if (!Files.exists(credentialsPath)) {
            throw new IllegalStateException(
                    "Le fichier de credentials Google est introuvable : " + credentialsPath
            );
        }

        try (InputStream inputStream = Files.newInputStream(credentialsPath)) {
            GoogleCredentials rawCredentials = GoogleCredentials.fromStream(inputStream);

            if (!(rawCredentials instanceof ServiceAccountCredentials serviceAccountCredentials)) {
                throw new IllegalStateException(
                        "Le fichier fourni n'est pas un JSON de service account Google."
                );
            }

            return serviceAccountCredentials
                    .createScoped(GOOGLE_WORKSPACE_SCOPES)
                    .createDelegated(properties.getDelegatedUser());
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "true")
    public Directory googleDirectoryService(
            HttpTransport googleHttpTransport,
            JsonFactory googleJsonFactory,
            GoogleCredentials googleCredentials,
            AppProperties properties
    ) {
        return new Directory.Builder(
                googleHttpTransport,
                googleJsonFactory,
                buildRequestInitializer(googleCredentials, properties)
        )
                .setApplicationName(properties.getApplicationName())
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "true")
    public Calendar googleCalendarService(
            HttpTransport googleHttpTransport,
            JsonFactory googleJsonFactory,
            GoogleCredentials googleCredentials,
            AppProperties properties
    ) {
        return new Calendar.Builder(
                googleHttpTransport,
                googleJsonFactory,
                buildRequestInitializer(googleCredentials, properties)
        )
                .setApplicationName(properties.getApplicationName())
                .build();
    }

    private HttpRequestInitializer buildRequestInitializer(GoogleCredentials credentials, AppProperties properties) {
        HttpCredentialsAdapter adapter = new HttpCredentialsAdapter(credentials);

        return request -> {
            adapter.initialize(request);
            request.setConnectTimeout(properties.getConnectTimeoutMs());
            request.setReadTimeout(properties.getReadTimeoutMs());
        };
    }

    private void validateProperties(AppProperties properties) {
        if (isBlank(properties.getDelegatedUser())) {
            throw new IllegalStateException("app.google.delegated-user est obligatoire quand Google est activé.");
        }

        if (isBlank(properties.getServiceAccountKeyPath())) {
            throw new IllegalStateException("app.google.service-account-key-path est obligatoire quand Google est activé.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}