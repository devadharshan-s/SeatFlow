package com.example.bookmyshow.DTO.TheatreDTO.SeatDTO;

import lombok.Data;

@Data
public class SeatDTO {
    private int seatNumber;
    private char rowNumber;
    private String category;
    private int price;
}
