package org.example.bookmyshowtheatreservice.theatre.exception;

import org.example.bookmyshowtheatreservice.common.exception.ServiceException;

public class SeatNotAvailableException extends ServiceException {

    public SeatNotAvailableException(String message) {
        super(message);
    }
}




