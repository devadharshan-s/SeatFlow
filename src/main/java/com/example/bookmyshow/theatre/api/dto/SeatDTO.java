package com.example.bookmyshow.theatre.api.dto;

import lombok.Data;

@Data
public class SeatDTO {
    private int seatNumber;
    private char rowNumber;
    private String category;
    private int price;
}




