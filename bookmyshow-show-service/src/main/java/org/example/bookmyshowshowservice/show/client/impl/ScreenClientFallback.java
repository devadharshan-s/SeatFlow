package org.example.bookmyshowshowservice.show.client.impl;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.show.client.ScreenClient;
import org.example.bookmyshowshowservice.show.client.dto.ScreenResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScreenClientFallback implements ScreenClient {

    @Override
    public ApiResponse<ScreenResponseDTO> getScreen(long screenId) {

        // Fallback response when theatre-service screen endpoint is unavailable
        ScreenResponseDTO fallbackScreen = new ScreenResponseDTO();
        fallbackScreen.setScreenId(screenId);
        fallbackScreen.setTheatreId(0L);

        return new ApiResponse<>(
                503,
                "Fallback: Unable to retrieve screen details at this time.",
                fallbackScreen,
                LocalDateTime.now()
        );

    }
}
