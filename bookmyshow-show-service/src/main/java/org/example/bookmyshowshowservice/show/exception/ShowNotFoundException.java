package org.example.bookmyshowshowservice.show.exception;

import org.example.bookmyshowshowservice.common.exception.ServiceException;

public class ShowNotFoundException extends ServiceException {

    public ShowNotFoundException(Long showId) {
        super("Show not found with id: " + showId);
    }

    public ShowNotFoundException(String message) {
        super(message);
    }
}






