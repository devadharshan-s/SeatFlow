package com.example.bookmyshow.show.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateShowRequestDTO {
    private int showId;
    private long theatreId;
    private int screenId;
    private LocalDateTime startTime;
    private double ticketPrice;
}




