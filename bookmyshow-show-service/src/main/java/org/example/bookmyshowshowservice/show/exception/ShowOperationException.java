package org.example.bookmyshowshowservice.show.exception;

import org.example.bookmyshowshowservice.common.exception.ServiceException;

public class ShowOperationException extends ServiceException {
    public ShowOperationException(String message) {
        super(message);
    }
}

