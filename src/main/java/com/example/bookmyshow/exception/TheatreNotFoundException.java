package com.example.bookmyshow.exception;

public class TheatreNotFoundException extends RuntimeException {
    public TheatreNotFoundException(String message) {
        super(message);
    }
}
