package com.example.bookmyshow.booking.exception;

import com.example.bookmyshow.exception.ServiceException;

public class TicketDeletionException extends ServiceException {

    public TicketDeletionException(String message) {
        super(message);
    }

    public TicketDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
