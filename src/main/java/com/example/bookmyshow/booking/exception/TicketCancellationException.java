package com.example.bookmyshow.booking.exception;

import com.example.bookmyshow.exception.ServiceException;

public class TicketCancellationException extends ServiceException {

    public TicketCancellationException(String message) {
        super(message);
    }

    public TicketCancellationException(String message, Throwable cause) {
        super(message, cause);
    }
}



