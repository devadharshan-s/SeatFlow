package com.example.bookmyshow.show.exception;

import com.example.bookmyshow.exception.ServiceException;

public class SeatNotAvailableException extends ServiceException {

    public SeatNotAvailableException(String message) {
        super(message);
    }
}



