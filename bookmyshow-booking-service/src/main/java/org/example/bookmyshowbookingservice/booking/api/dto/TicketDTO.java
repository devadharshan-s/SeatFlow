package org.example.bookmyshowbookingservice.booking.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class TicketDTO {

    private Long ticketId;
    private String bookingToken;
    private long showId;
    private List<Long> seatIds;
    private List<Long> showSeatIds;
    private Long userId;
    private double amountPaid;

}
