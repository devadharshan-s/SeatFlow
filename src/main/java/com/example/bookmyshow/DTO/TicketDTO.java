package com.example.bookmyshow.DTO;

import lombok.Data;

import java.util.List;

@Data
public class TicketDTO {

    private long showId;
    private List<Long> seatIds;
}
