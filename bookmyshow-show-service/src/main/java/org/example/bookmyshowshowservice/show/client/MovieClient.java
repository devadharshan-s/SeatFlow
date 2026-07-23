package org.example.bookmyshowshowservice.show.client;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.show.client.dto.MovieResponseDTO;
import org.example.bookmyshowshowservice.show.client.impl.MovieClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "bookmyshow-movie-service", fallback = MovieClientFallback.class)
public interface MovieClient {

    @GetMapping("/getMovie/{movieId}")
    ApiResponse<MovieResponseDTO> getMovie(@PathVariable("movieId") Long movieId);
}
