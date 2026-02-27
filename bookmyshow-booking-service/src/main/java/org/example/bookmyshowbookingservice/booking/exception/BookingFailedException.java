package org.example.bookmyshowbookingservice.booking.exception;

import org.example.bookmyshowbookingservice.common.exception.ServiceException;

public class BookingFailedException extends ServiceException {

    public BookingFailedException(String message) {
        super(message);
    }

    public BookingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}




