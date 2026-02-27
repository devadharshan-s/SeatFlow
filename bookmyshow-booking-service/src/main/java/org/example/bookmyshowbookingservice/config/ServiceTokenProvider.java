package org.example.bookmyshowbookingservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ServiceTokenProvider {

    private final ServiceAuthProperties authProperties;
    private final RestClient restClient = RestClient.create();

    private String accessToken;
    private Instant expiresAt = Instant.EPOCH;

    public synchronized String getAccessToken() {
        if (accessToken != null && Instant.now().isBefore(expiresAt.minusSeconds(30))) {
            return accessToken;
        }

        if (isBlank(authProperties.getTokenUrl()) || isBlank(authProperties.getClientId()) || isBlank(authProperties.getClientSecret())) {
            throw new IllegalStateException("services.auth token configuration is missing");
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", authProperties.getClientId());
        form.add("client_secret", authProperties.getClientSecret());

        @SuppressWarnings("unchecked")
        Map<String, Object> tokenResponse = restClient.post()
                .uri(authProperties.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        if (tokenResponse == null || tokenResponse.get("access_token") == null) {
            throw new IllegalStateException("Unable to fetch access token for inter-service call");
        }

        accessToken = String.valueOf(tokenResponse.get("access_token"));

        Object expiresInValue = tokenResponse.get("expires_in");
        long expiresInSeconds = expiresInValue instanceof Number number ? number.longValue() : 60L;
        expiresAt = Instant.now().plusSeconds(expiresInSeconds);

        return accessToken;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
