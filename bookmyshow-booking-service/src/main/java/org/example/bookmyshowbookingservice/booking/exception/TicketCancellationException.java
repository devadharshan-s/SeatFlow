package org.example.bookmyshowbookingservice.booking.exception;

import org.example.bookmyshowbookingservice.common.exception.ServiceException;

public class TicketCancellationException extends ServiceException {

    public TicketCancellationException(String message) {
        super(message);
    }

    public TicketCancellationException(String message, Throwable cause) {
        super(message, cause);
    }
}




