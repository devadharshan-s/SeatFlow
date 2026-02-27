package org.example.bookmyshowtheatreservice.common.exception;

import org.example.bookmyshowtheatreservice.common.dto.ApiResponse;
import org.example.bookmyshowtheatreservice.theatre.exception.ScreenNotFoundException;
import org.example.bookmyshowtheatreservice.theatre.exception.SeatNotAvailableException;
import org.example.bookmyshowtheatreservice.theatre.exception.SeatNotFoundException;
import org.example.bookmyshowtheatreservice.theatre.exception.TheatreNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            TheatreNotFoundException.class,
            ScreenNotFoundException.class,
            SeatNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ServiceException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(SeatNotAvailableException ex) {
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

