package com.example.bookmyshow.movie.client.impl;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.movie.client.ShowClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component("movieShowRestClient")
@RequiredArgsConstructor
public class ShowRestClient implements ShowClient {

    private final RestClient restClient;

    @Override
    public List<Long> getAllShows() {
        ApiResponse<List<Long>> response = restClient.get()
                .uri("/getAllShows")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return response.getData();
    }
}
