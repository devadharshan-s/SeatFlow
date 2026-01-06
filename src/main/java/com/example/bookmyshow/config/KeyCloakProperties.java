package com.example.bookmyshow.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeyCloakProperties {

    private String authServerUrl;
    private String realm;
    private Admin admin = new Admin();

    @Getter @Setter
    public static class Admin {
        private String clientId;
        private String clientSecret;
    }

}
