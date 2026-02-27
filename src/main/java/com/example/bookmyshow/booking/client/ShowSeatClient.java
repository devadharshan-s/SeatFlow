package com.example.bookmyshow.booking.client;

import com.example.bookmyshow.show.api.dto.SeatAvailabilityResponse;

import java.util.List;

public interface ShowSeatClient {
    List<SeatAvailabilityResponse> seats(Long showId, String status);
}




