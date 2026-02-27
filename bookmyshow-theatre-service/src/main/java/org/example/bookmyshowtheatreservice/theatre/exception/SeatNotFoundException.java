package org.example.bookmyshowtheatreservice.theatre.exception;

import org.example.bookmyshowtheatreservice.common.exception.ServiceException;

public class SeatNotFoundException extends ServiceException {

    public SeatNotFoundException(Long seatId) {
        super("Seat not found with id: " + seatId);
    }

    public SeatNotFoundException(String message) {
        super(message);
    }
}




