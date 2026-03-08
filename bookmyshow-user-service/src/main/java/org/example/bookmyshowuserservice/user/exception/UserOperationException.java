package org.example.bookmyshowuserservice.user.exception;

import org.example.bookmyshowuserservice.common.exception.ServiceException;

public class UserOperationException extends ServiceException {
    public UserOperationException(String message) {
        super(message);
    }

    public UserOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

