package org.example.bookmyshowshowservice.show.client.impl;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.show.client.SeatClient;
import org.example.bookmyshowshowservice.show.client.dto.SeatResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component("showModuleSeatClientFallback")
public class SeatClientFallback implements SeatClient {

    @Override
    public ApiResponse<Map<Long, SeatResponseDTO>> getSeats(List<Long> seatIds) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to retrieve seat details at this time.",
                Collections.emptyMap(),
                LocalDateTime.now()
        );

    }

    @Override
    public ApiResponse<List<SeatResponseDTO>> getSeatsByScreen(Long screenId) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to retrieve seats for screen at this time.",
                Collections.emptyList(),
                LocalDateTime.now()
        );

    }
}
