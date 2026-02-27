package com.example.bookmyshow.theatre.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class SeatRequestDTO {
    private int screenId;
    private List<SeatDTO> seats;
}




