package com.example.bookmyshow.DTO.TheatreDTO.SeatDTO;

import lombok.Data;

import java.util.List;

@Data
public class SeatRequestDTO {
    private int screenId;
    private List<SeatDTO> seats;
}
