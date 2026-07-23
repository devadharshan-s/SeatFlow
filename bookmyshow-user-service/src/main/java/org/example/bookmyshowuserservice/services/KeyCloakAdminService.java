package org.example.bookmyshowuserservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmyshowuserservice.config.KeyCloakProperties;
import org.example.bookmyshowuserservice.user.exception.UserOperationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyCloakAdminService {

    private final KeyCloakProperties keyCloakProperties;
    private final RestClient restClient = RestClient.create();

    private String getAdminAccessToken() {
        String tokenUrl = keyCloakProperties.getAuthServerUrl() +
                "/realms/" + keyCloakProperties.getRealm() +
                "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", keyCloakProperties.getAdmin().getClientId());
        formData.add("client_secret", keyCloakProperties.getAdmin().getClientSecret());
        formData.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(tokenUrl)
                    .headers(h -> h.addAll(headers))
                    .body(formData)
                    .retrieve()
                    .body(Map.class);

            return response != null ? (String) response.get("access_token") : null;
        } catch (Exception e) {
            throw new UserOperationException("Unable to obtain Keycloak admin token", e);
        }
    }

    public String createUser(String username, String email, String password, String firstName, String lastName, String phone) {
        String token = getAdminAccessToken();
        if (token == null || token.isBlank()) {
            throw new UserOperationException("Failed to authenticate with Keycloak admin client");
        }

        String createUserUrl = keyCloakProperties.getAuthServerUrl() + "/admin/realms/" + keyCloakProperties.getRealm() + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        String userJson = String.format("""
                {
                  "username": "%s",
                  "email": "%s",
                  "firstName": "%s",
                  "lastName": "%s",
                  "enabled": true,
                  "emailVerified": true,
                  "attributes": {
                    "phone": ["%s"]
                  },
                  "requiredActions": [],
                  "credentials": [{
                      "type": "password",
                      "value": "%s",
                      "temporary": false
                  }]
                }
                """, username, email, firstName, lastName, phone == null ? "" : phone, password);

        try {
            ResponseEntity<Void> response = restClient.method(HttpMethod.POST)
                    .uri(createUserUrl)
                    .headers(h -> h.addAll(headers))
                    .body(userJson)
                    .retrieve()
                    .toBodilessEntity();

            URI location = response.getHeaders().getLocation();
            if (location == null) {
                throw new UserOperationException("Keycloak user created but id not returned");
            }

            return location.toString().substring(location.toString().lastIndexOf('/') + 1);
        } catch (Exception ex) {
            throw new UserOperationException("Failed to create user in Keycloak", ex);
        }
    }

    public void deleteUser(String userId) {
        String token = getAdminAccessToken();
        if (token == null || token.isBlank()) {
            throw new UserOperationException("Failed to authenticate with Keycloak admin client");
        }

        String deleteUrl = keyCloakProperties.getAuthServerUrl() +
                "/admin/realms/" + keyCloakProperties.getRealm() + "/users/" + userId;

        try {
            restClient.method(HttpMethod.DELETE)
                    .uri(deleteUrl)
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Deleted user from Keycloak: {}", userId);
        } catch (Exception ex) {
            throw new UserOperationException("Failed to delete user in Keycloak", ex);
        }
    }
}

