package com.example.bookmyshow.DTO.TheatreDTO.SeatDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeatAvailabilityDTO {
    private long seatId;
    private String rowNumber;
    private int seatNumber;
    private String status;
    private boolean booked;
    private LocalDateTime lockedUntil;
    private int price;
    private String category;
}
