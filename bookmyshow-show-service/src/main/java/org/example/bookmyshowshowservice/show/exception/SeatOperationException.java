package org.example.bookmyshowshowservice.show.exception;

import org.example.bookmyshowshowservice.common.exception.ServiceException;

public class SeatOperationException extends ServiceException {

    public SeatOperationException(String message) {
        super(message);
    }

    public SeatOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}





