package com.example.bookmyshow.services;

import com.example.bookmyshow.config.KeyCloakProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyCloakAdminService {

    private final KeyCloakProperties keyCloakProperties;
    private final RestTemplate restTemplate = new RestTemplate();

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
            Map<String, Object> response = restTemplate.postForObject(tokenUrl, request, Map.class);
            log.info("✅ Successfully retrieved Keycloak admin token.");
            return (String) response.get("access_token");
        } catch (Exception e) {
            log.error("❌ Failed to get admin token from Keycloak: {}", e.getMessage());
            throw new RuntimeException("Unable to obtain Keycloak admin token", e);
        }
    }

    public String createUser(String username, String email, String password, String firstName, String lastName, String phone) {
        log.info(username + " " + email + " " + password);
        String token = getAdminAccessToken();

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
    """, username, email, firstName, lastName, phone, password);

        HttpEntity<String> request = new HttpEntity<>(userJson, headers);
        ResponseEntity<Void> response = restTemplate.exchange(createUserUrl, HttpMethod.POST, request, Void.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String location = response.getHeaders().getLocation().toString();
            String keycloakId = location.substring(location.lastIndexOf("/") + 1);
            log.info("✅ User '{}' created in Keycloak successfully.", username);
            return keycloakId;
        } else {
            log.error("❌ Failed to create user in Keycloak. Status: {}", response.getStatusCode());
            return null;
        }
    }

    // 3️⃣ Get all users
    public Map[] getAllUsers() {
        String token = getAdminAccessToken();

        String getUsersUrl = keyCloakProperties.getAuthServerUrl()+ "/admin/realms/" + keyCloakProperties.getRealm() + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map[]> response = restTemplate.exchange(getUsersUrl, HttpMethod.GET, entity, Map[].class);

        return response.getBody() != null ? response.getBody() : new Map[0];
    }

    // 4️⃣ Delete a user
    public void deleteUser(String userId) {
        String token = getAdminAccessToken();
        String deleteUrl = keyCloakProperties.getAuthServerUrl() + "/admin/realms/" + keyCloakProperties.getRealm() + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, Void.class);
        log.info("🗑️ Deleted user from KeyCloak{}", userId);
    }
}
