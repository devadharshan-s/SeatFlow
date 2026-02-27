package com.example.bookmyshow.booking.exception;

import com.example.bookmyshow.exception.ServiceException;

public class InvalidShowIdException extends ServiceException {

    public InvalidShowIdException(Long showId) {
        super("Invalid showId: " + showId);
    }

    public InvalidShowIdException(String message) {
        super(message);
    }
}
