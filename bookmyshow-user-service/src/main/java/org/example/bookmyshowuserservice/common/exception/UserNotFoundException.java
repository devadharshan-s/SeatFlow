package org.example.bookmyshowuserservice.common.exception;

public class UserNotFoundException extends ServiceException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

