package org.example.bookmyshowbookingservice.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.example.bookmyshowbookingservice.common.exception.DownstreamServiceException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor bearerTokenInterceptor(ServiceTokenProvider tokenProvider) {
        return template -> template.header("Authorization", "Bearer " + tokenProvider.getAccessToken());
    }

    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return (methodKey, response) -> new DownstreamServiceException(
                response.status(),
                extractMessage(response, objectMapper)
        );
    }

    private String extractMessage(Response response, ObjectMapper objectMapper) {
        String defaultMessage = "Downstream service call failed";

        if (response == null || response.body() == null) {
            return defaultMessage;
        }

        try (InputStream inputStream = response.body().asInputStream()) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(body);
            if (node.hasNonNull("message")) {
                return node.get("message").asText();
            }
            return defaultMessage;
        } catch (IOException e) {
            return defaultMessage;
        }
    }
}
