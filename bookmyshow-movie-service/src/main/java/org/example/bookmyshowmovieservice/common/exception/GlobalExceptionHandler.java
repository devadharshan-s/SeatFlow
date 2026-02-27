package org.example.bookmyshowmovieservice.common.exception;

import org.example.bookmyshowmovieservice.common.dto.ApiResponse;
import org.example.bookmyshowmovieservice.movie.exception.GenreOperationException;
import org.example.bookmyshowmovieservice.movie.exception.PersonOperationException;
import org.example.bookmyshowmovieservice.movie.model.exception.MovieNotFoundException;
import org.example.bookmyshowmovieservice.movie.model.exception.PersonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MovieNotFoundException.class, PersonNotFoundException.class})
    public ResponseEntity<ApiResponse<Void>> handleNotFound(RuntimeException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({GenreOperationException.class, PersonOperationException.class, IllegalStateException.class})
    public ResponseEntity<ApiResponse<Void>> handleConflict(RuntimeException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnhandled(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    private ResponseEntity<ApiResponse<Void>> build(HttpStatus status, String message) {
        ApiResponse<Void> response = new ApiResponse<>(
                status.value(),
                message,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(response);
    }
}

