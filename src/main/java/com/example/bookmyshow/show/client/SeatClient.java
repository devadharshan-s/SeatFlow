package com.example.bookmyshow.show.client;

import com.example.bookmyshow.theatre.api.dto.SeatResponseDTO;

import java.util.List;
import java.util.Map;

public interface SeatClient {
    Map<Long, SeatResponseDTO> getSeats(List<Long> seatIds) ;

    List<SeatResponseDTO> getSeatsByScreen(Long screenId);
}




