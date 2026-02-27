package com.example.bookmyshow.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient restClient(
            RestClient.Builder builder,
            @Value("${app.internal.base-url:http://localhost:${server.port}}") String internalBaseUrl
    ) {
        return builder
                .baseUrl(internalBaseUrl)
                .requestInterceptor((request, body, execution) -> {
                    if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                        String token = resolveBearerToken();
                        if (token != null) {
                            request.getHeaders().setBearerAuth(token);
                        }
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    private String resolveBearerToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getTokenValue();
        }
        return null;
    }
}
