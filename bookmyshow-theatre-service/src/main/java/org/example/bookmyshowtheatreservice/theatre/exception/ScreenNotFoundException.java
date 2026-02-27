package org.example.bookmyshowtheatreservice.theatre.exception;

import org.example.bookmyshowtheatreservice.common.exception.ServiceException;

public class ScreenNotFoundException extends ServiceException {
    public ScreenNotFoundException(String message) { super(message); }
}
