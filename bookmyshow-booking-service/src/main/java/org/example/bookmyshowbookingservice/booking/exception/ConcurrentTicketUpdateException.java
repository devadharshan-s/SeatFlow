package org.example.bookmyshowbookingservice.booking.exception;

import org.example.bookmyshowbookingservice.common.exception.ServiceException;

public class ConcurrentTicketUpdateException extends ServiceException {
    public ConcurrentTicketUpdateException(String message) {
        super(message);
    }

    public ConcurrentTicketUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

