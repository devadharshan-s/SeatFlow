package org.example.bookmyshowmovieservice.movie.model.exception;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(String message) {
        super(message);
    }
}
