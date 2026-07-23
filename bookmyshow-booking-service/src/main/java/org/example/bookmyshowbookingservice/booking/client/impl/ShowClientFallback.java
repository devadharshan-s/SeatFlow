package org.example.bookmyshowbookingservice.booking.client.impl;

import org.example.bookmyshowbookingservice.booking.client.ShowClient;
import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ShowClientFallback implements ShowClient {

    @Override
    public ApiResponse<Object> getShowById(Long showId) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to retrieve show details for ID: " + showId,
                null,
                LocalDateTime.now()
        );

    }
}
