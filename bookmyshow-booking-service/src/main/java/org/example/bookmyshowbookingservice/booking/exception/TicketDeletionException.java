package org.example.bookmyshowbookingservice.booking.exception;

import org.example.bookmyshowbookingservice.common.exception.ServiceException;

public class TicketDeletionException extends ServiceException {

    public TicketDeletionException(String message) {
        super(message);
    }

    public TicketDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}

