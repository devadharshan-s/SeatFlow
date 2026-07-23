package org.example.bookmyshowshowservice.show.client.impl;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.show.client.MovieClient;
import org.example.bookmyshowshowservice.show.client.dto.MovieResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MovieClientFallback implements MovieClient {

    @Override
    public ApiResponse<MovieResponseDTO> getMovie(Long movieId) {

        // Fallback response when movie-service is unavailable or circuit breaker triggers
        MovieResponseDTO fallbackMovie = new MovieResponseDTO();
        fallbackMovie.setMovieId(movieId);

        return new ApiResponse<>(
                503,
                "Fallback: Unable to retrieve movie details at this time.",
                fallbackMovie,
                LocalDateTime.now()
        );

    }
}
