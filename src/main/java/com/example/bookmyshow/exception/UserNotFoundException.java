package com.example.bookmyshow.exception;

public class UserNotFoundException extends ServiceException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
