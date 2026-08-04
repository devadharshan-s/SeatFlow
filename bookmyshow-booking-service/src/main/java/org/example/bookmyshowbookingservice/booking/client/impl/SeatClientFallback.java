package org.example.bookmyshowbookingservice.booking.client.impl;

import org.example.bookmyshowbookingservice.booking.client.SeatClient;
import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class SeatClientFallback implements SeatClient {

    @Override
    public ApiResponse<List<Long>> resolveShowSeatIds(Long showId, List<Long> seatIds) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to resolve show seat IDs at this time.",
                Collections.emptyList(),
                LocalDateTime.now());

    }

    @Override
    public ApiResponse<List<Long>> lockSeats(int seconds, List<Long> seatIds) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to lock seats at this time.",
                Collections.emptyList(),
                LocalDateTime.now());

    }

    @Override
    public ApiResponse<List<Long>> bookSeats(String bookingToken, List<Long> seatIds) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to book seats at this time.",
                Collections.emptyList(),
                LocalDateTime.now());

    }

    @Override
    public ApiResponse<Boolean> unlockSeats(Long ticketId, List<Long> seatIds) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to unlock seats at this time.",
                false,
                LocalDateTime.now());

    }

    @Override
    public ApiResponse<List<Long>> cancelSeats(Long ticketId) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to cancel seats at this time.",
                Collections.emptyList(),
                LocalDateTime.now());

    }

    @Override
    public ApiResponse<List<Long>> holdSeats(String bookingToken, int holdSeconds, List<Long> seatIds) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to hold seats at this time.",
                Collections.emptyList(),
                LocalDateTime.now());

    }

    @Override
    public ApiResponse<Boolean> releaseHold(String bookingToken) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to release seat hold at this time.",
                false,
                LocalDateTime.now());

    }

    @Override
    public ApiResponse<List<Long>> confirmHold(String bookingToken) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to confirm seat hold at this time.",
                Collections.emptyList(),
                LocalDateTime.now());

    }
}
