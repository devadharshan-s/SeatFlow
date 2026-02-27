package org.example.bookmyshowbookingservice.booking.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatAvailabilityResponse {
    private Long showSeatId;
    private Long seatId;
    private String status;
}

