package com.example.bookmyshow.show.exception;

import com.example.bookmyshow.exception.ServiceException;

public class ShowNotFoundException extends ServiceException {

    public ShowNotFoundException(Long showId) {
        super("Show not found with id: " + showId);
    }

    public ShowNotFoundException(String message) {
        super(message);
    }
}





