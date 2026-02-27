package com.example.bookmyshow.theatre.exception;

import com.example.bookmyshow.exception.ServiceException;

public class SeatNotAvailableException extends ServiceException {

    public SeatNotAvailableException(String message) {
        super(message);
    }
}



