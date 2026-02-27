package org.example.bookmyshowpaymentservice.payment.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.example.bookmyshowpaymentservice.common.exception.DownstreamServiceException;
import org.example.bookmyshowpaymentservice.common.exception.TicketNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class TicketClientFeignConfig {

    @Bean
    public RequestInterceptor ticketClientAuthInterceptor(ServiceTokenProvider tokenProvider) {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + tokenProvider.getAccessToken());
    }

    @Bean
    public ErrorDecoder ticketClientErrorDecoder(ObjectMapper objectMapper) {
        return (String methodKey, Response response) -> {
            String message = extractMessage(response, objectMapper);
            if (response.status() == 404) {
                return new TicketNotFoundException(message);
            }
            return new DownstreamServiceException(response.status(), message);
        };
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
