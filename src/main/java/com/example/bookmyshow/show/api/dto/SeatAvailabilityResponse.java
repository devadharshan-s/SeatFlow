package com.example.bookmyshow.show.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeatAvailabilityResponse {
    private long seatId;
    private String rowNumber;
    private int seatNumber;
    private String status;
    private boolean booked;
    private LocalDateTime lockedUntil;
    private int price;
    private String category;
}




