package com.example.bookmyshow.show.client.impl;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.exception.MovieNotFoundException;
import com.example.bookmyshow.movie.api.dto.MovieResponseDTO;
import com.example.bookmyshow.show.client.MovieClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class MovieRestClient implements MovieClient {

    private final RestClient restClient;

    @Override
    public MovieResponseDTO getMovie(Long movieId) {
        ApiResponse<MovieResponseDTO> response = restClient.get()
                .uri("/getMovie/{movieId}", movieId)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<MovieResponseDTO>>() {});

        if (response == null || response.getData() == null) {
            throw new MovieNotFoundException("Movie not found for id: " + movieId);
        }

        return response.getData();
    }
}
