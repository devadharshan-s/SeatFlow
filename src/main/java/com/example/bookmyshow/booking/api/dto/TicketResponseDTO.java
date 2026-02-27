package com.example.bookmyshow.booking.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class TicketResponseDTO {

    private long ticketId;
    private List<Long> showSeatIds;
    private Long showId;
    private Long userId;
    private double amountPaid;
}
