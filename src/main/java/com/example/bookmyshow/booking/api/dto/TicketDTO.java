package com.example.bookmyshow.booking.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class TicketDTO {

    private long showId;
    private List<Long> seatIds;
}




