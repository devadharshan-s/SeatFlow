package com.example.bookmyshow.theatre.exception;

import com.example.bookmyshow.exception.ServiceException;

public class SeatNotFoundException extends ServiceException {

    public SeatNotFoundException(Long seatId) {
        super("Seat not found with id: " + seatId);
    }

    public SeatNotFoundException(String message) {
        super(message);
    }
}



