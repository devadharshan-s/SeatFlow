package org.example.bookmyshowmovieservice.movie.client.impl;

import org.example.bookmyshowmovieservice.common.dto.ApiResponse;
import org.example.bookmyshowmovieservice.movie.client.ShowClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class ShowClientFallback implements ShowClient {

    @Override
    public ApiResponse<List<Long>> getAllShows(int page, int size) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to retrieve shows list at this time.",
                Collections.emptyList(),
                LocalDateTime.now()
        );

    }
}
