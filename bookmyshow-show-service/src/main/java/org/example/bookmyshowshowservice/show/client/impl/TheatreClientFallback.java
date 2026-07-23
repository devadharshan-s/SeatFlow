package org.example.bookmyshowshowservice.show.client.impl;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.show.client.TheatreClient;
import org.example.bookmyshowshowservice.show.client.dto.TheatreDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TheatreClientFallback implements TheatreClient {

    @Override
    public ApiResponse<TheatreDTO> getTheatre(Long theatreId) {

        // Fallback response when theatre-service is unavailable or circuit breaker triggers
        TheatreDTO fallbackTheatre = new TheatreDTO();
        fallbackTheatre.setTheatreId(theatreId);

        return new ApiResponse<>(
                503,
                "Fallback: Unable to retrieve theatre details at this time.",
                fallbackTheatre,
                LocalDateTime.now()
        );

    }
}
