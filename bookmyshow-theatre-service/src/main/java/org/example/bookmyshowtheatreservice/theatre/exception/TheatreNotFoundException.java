package org.example.bookmyshowtheatreservice.theatre.exception;

import org.example.bookmyshowtheatreservice.common.exception.ServiceException;

public class TheatreNotFoundException extends ServiceException {
    public TheatreNotFoundException(String message) { super(message); }
}
