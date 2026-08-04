package org.example.bookmyshowshowservice.show.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SeatAvailabilityResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private long seatId;
    private String rowNumber;
    private int seatNumber;
    private String status;
    private boolean booked;
    private LocalDateTime lockedUntil;
    private int price;
    private String category;
}





