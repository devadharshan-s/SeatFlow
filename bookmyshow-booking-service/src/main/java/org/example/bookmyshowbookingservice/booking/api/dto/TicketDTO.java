package org.example.bookmyshowbookingservice.booking.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class TicketDTO {

    private long showId;
    private List<Long> seatIds;
}





