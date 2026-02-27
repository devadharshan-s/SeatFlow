package org.example.bookmyshowbookingservice.booking.exception;

import org.example.bookmyshowbookingservice.common.exception.ServiceException;

public class InvalidShowIdException extends ServiceException {

    public InvalidShowIdException(Long showId) {
        super("Invalid showId: " + showId);
    }

    public InvalidShowIdException(String message) {
        super(message);
    }
}

