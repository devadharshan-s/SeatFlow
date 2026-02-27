package org.example.bookmyshowshowservice.common.exception;

import feign.FeignException;
import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.show.exception.SeatNotAvailableException;
import org.example.bookmyshowshowservice.show.exception.SeatNotFoundException;
import org.example.bookmyshowshowservice.show.exception.SeatOperationException;
import org.example.bookmyshowshowservice.show.exception.ShowNotFoundException;
import org.example.bookmyshowshowservice.show.exception.ShowOperationException;
import org.example.bookmyshowshowservice.show.exception.ShowSeatNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ShowNotFoundException.class,
            SeatNotFoundException.class,
            ShowSeatNotFoundException.class,
            MovieNotFoundException.class,
            ScreenNotFoundException.class,
            TheatreNotFoundException.class,
            TicketNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleNotFound(RuntimeException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({SeatNotAvailableException.class, ShowOperationException.class, SeatOperationException.class})
    public ResponseEntity<ApiResponse<Void>> handleConflict(ServiceException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Void>> handleFeign(FeignException ex) {
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            status = HttpStatus.BAD_GATEWAY;
        }
        return build(status, "Downstream service call failed");
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
