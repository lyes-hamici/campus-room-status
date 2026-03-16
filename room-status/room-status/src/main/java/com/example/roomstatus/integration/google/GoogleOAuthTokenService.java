package com.example.roomstatus.integration.google;

import com.example.roomstatus.config.AppProperties;
import com.example.roomstatus.exception.GoogleIntegrationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.google", name = "enabled", havingValue = "true")
public class GoogleOAuthTokenService {

    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final AppProperties properties;

    public GoogleOAuthTokenService(OAuth2AuthorizedClientManager authorizedClientManager, AppProperties properties) {
        this.authorizedClientManager = authorizedClientManager;
        this.properties = properties;
    }

    public String getAccessTokenValue() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new GoogleIntegrationException(
                    "Authentification Google requise. Connecte-toi d'abord via /oauth2/authorization/google"
            );
        }

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(properties.getRegistrationId())
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
            throw new GoogleIntegrationException(
                    "Impossible de récupérer un access token Google. Vérifie la connexion OAuth2."
            );
        }

        return authorizedClient.getAccessToken().getTokenValue();
    }
}