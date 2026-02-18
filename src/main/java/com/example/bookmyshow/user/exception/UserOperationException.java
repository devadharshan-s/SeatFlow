package com.example.bookmyshow.user.exception;

import com.example.bookmyshow.exception.ServiceException;

public class UserOperationException extends ServiceException {

    public UserOperationException(String message) {
        super(message);
    }

    public UserOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
