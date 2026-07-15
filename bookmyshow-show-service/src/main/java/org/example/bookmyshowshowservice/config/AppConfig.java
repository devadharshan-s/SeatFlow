package org.example.bookmyshowshowservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    private RestClient buildServiceClient(RestClient.Builder builder, String baseUrl, ServiceTokenProvider tokenProvider) {
        return builder
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().setBearerAuth(tokenProvider.getAccessToken());
                    return execution.execute(request, body);
                })
                .build();
    }

    @Bean
    public RestClient movieRestClient(RestClient.Builder builder, @Value("${services.movie.base-url}") String baseUrl, ServiceTokenProvider tokenProvider) {
        return buildServiceClient(builder, baseUrl, tokenProvider);
    }

    @Bean
    public RestClient theatreRestClient(RestClient.Builder builder, @Value("${services.theatre.base-url}") String baseUrl, ServiceTokenProvider tokenProvider) {
        return buildServiceClient(builder, baseUrl, tokenProvider);
    }

    @Bean
    public RestClient bookingRestClient(RestClient.Builder builder, @Value("${services.booking.base-url}") String baseUrl, ServiceTokenProvider tokenProvider) {
        return buildServiceClient(builder, baseUrl, tokenProvider);
    }
}
