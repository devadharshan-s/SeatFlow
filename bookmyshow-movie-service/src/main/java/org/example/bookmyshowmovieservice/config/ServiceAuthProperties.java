package org.example.bookmyshowmovieservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "services.auth")
@Getter
@Setter
public class ServiceAuthProperties {
    private String tokenUrl;
    private String clientId;
    private String clientSecret;
}
