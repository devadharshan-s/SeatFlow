package org.example.bookmyshowbookingservice.booking.client.impl;

import org.example.bookmyshowbookingservice.booking.api.dto.SeatAvailabilityResponse;
import org.example.bookmyshowbookingservice.booking.client.ShowSeatClient;
import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class ShowSeatClientFallback implements ShowSeatClient {

    @Override
    public ApiResponse<List<SeatAvailabilityResponse>> seats(Long showId, String status) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to retrieve seat availability for show ID: " + showId,
                Collections.emptyList(),
                LocalDateTime.now()
        );

    }
}
