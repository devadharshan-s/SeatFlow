package com.example.bookmyshow.booking.client.impl;

import com.example.bookmyshow.booking.client.ShowClient;
import com.example.bookmyshow.booking.exception.BookingFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("bookingShowRestClient")
@RequiredArgsConstructor
public class ShowRestClient implements ShowClient {

    private final RestClient restClient;

    @Override
    public void validateShowExists(Long showId) {
        try {
            restClient.get()
                    .uri("/getShowById/{showId}", showId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            throw new BookingFailedException(
                    "Show validation failed for showId: " + showId,
                    ex
            );
        }
    }
}
