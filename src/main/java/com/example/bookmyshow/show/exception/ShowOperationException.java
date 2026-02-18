package com.example.bookmyshow.show.exception;

import com.example.bookmyshow.exception.ServiceException;

public class ShowOperationException extends ServiceException {
    public ShowOperationException(String message) {
        super(message);
    }
}
