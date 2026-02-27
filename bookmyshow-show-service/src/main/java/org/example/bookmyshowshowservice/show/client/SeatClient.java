package org.example.bookmyshowshowservice.show.client;

import org.example.bookmyshowshowservice.show.client.dto.SeatResponseDTO;

import java.util.List;
import java.util.Map;

public interface SeatClient {
    Map<Long, SeatResponseDTO> getSeats(List<Long> seatIds) ;

    List<SeatResponseDTO> getSeatsByScreen(Long screenId);
}





