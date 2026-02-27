package com.example.bookmyshow.movie.exception;

public class GenreOperationException extends RuntimeException {

    public GenreOperationException(String message) {
        super(message);
    }

    public GenreOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}




