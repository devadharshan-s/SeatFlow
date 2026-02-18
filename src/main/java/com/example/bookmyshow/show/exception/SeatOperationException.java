package com.example.bookmyshow.show.exception;

import com.example.bookmyshow.exception.ServiceException;

public class SeatOperationException extends ServiceException {

    public SeatOperationException(String message) {
        super(message);
    }

    public SeatOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}




