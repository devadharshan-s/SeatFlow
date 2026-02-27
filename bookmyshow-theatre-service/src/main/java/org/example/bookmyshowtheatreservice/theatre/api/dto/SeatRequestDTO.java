package org.example.bookmyshowtheatreservice.theatre.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class SeatRequestDTO {
    private int screenId;
    private List<SeatDTO> seats;
}





