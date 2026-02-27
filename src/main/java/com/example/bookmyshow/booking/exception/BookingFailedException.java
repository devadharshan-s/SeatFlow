package com.example.bookmyshow.booking.exception;

import com.example.bookmyshow.exception.ServiceException;

public class BookingFailedException extends ServiceException {

    public BookingFailedException(String message) {
        super(message);
    }

    public BookingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}



