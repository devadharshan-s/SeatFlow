package com.example.bookmyshow.theatre.api.dto;

import lombok.Data;

@Data
public class SeatResponseDTO {
    private long seatId;
    private int seatNumber;
    private char rowNumber;
    private String category;
    private int price;
}




