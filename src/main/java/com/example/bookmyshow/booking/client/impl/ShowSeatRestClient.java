package com.example.bookmyshow.booking.client.impl;

import com.example.bookmyshow.booking.client.ShowSeatClient;
import com.example.bookmyshow.booking.exception.BookingFailedException;
import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.show.api.dto.SeatAvailabilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ShowSeatRestClient implements ShowSeatClient {

    private final RestClient restClient;

    @Override
    public List<SeatAvailabilityResponse> seats(Long showId, String status) {
        try {
            ApiResponse<List<SeatAvailabilityResponse>> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getShowSeats/{showId}")
                            .queryParam("status", status)
                            .build(showId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response == null || response.getData() == null) {
                throw new BookingFailedException("Seat lookup returned empty response for showId: " + showId);
            }

            return response.getData();
        } catch (Exception ex) {
            throw new BookingFailedException(
                    "Seat lookup failed for showId: " + showId,
                    ex
            );
        }
    }
}
