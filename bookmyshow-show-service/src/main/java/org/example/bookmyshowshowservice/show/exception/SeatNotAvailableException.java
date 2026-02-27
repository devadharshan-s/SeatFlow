package org.example.bookmyshowshowservice.show.exception;

import org.example.bookmyshowshowservice.common.exception.ServiceException;

public class SeatNotAvailableException extends ServiceException {

    public SeatNotAvailableException(String message) {
        super(message);
    }
}




