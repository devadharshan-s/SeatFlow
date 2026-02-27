package com.example.bookmyshow;

import com.example.bookmyshow.booking.exception.InvalidShowIdException;
import com.example.bookmyshow.booking.exception.TicketCancellationException;
import com.example.bookmyshow.booking.exception.TicketDeletionException;
import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.exception.MovieNotFoundException;
import com.example.bookmyshow.exception.ScreenNotFoundException;
import com.example.bookmyshow.exception.TheatreNotFoundException;
import com.example.bookmyshow.exception.TicketNotFoundException;
import com.example.bookmyshow.exception.UserNotFoundException;
import com.example.bookmyshow.movie.exception.GenreOperationException;
import com.example.bookmyshow.movie.exception.PersonOperationException;
import com.example.bookmyshow.movie.model.exception.PersonNotFoundException;
import com.example.bookmyshow.show.exception.SeatNotAvailableException;
import com.example.bookmyshow.show.exception.SeatNotFoundException;
import com.example.bookmyshow.show.exception.SeatOperationException;
import com.example.bookmyshow.show.exception.ShowNotFoundException;
import com.example.bookmyshow.show.exception.ShowOperationException;
import com.example.bookmyshow.show.exception.ShowSeatNotFoundException;
import com.example.bookmyshow.user.exception.UserOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ShowNotFoundException.class,
            TicketNotFoundException.class,
            SeatNotFoundException.class,
            MovieNotFoundException.class,
            PersonNotFoundException.class,
            ShowSeatNotFoundException.class,
            TheatreNotFoundException.class,
            ScreenNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ApiResponse<?>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(
                        404,
                        ex.getMessage(),
                        null,
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<ApiResponse<?>> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(
                        409,
                        ex.getMessage(),
                        null,
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler({
            GenreOperationException.class,
            PersonOperationException.class,
            ShowOperationException.class,
            InvalidShowIdException.class,
            UserOperationException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiResponse<?>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        400,
                        ex.getMessage(),
                        null,
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler({
            SeatOperationException.class,
            TicketCancellationException.class,
            TicketDeletionException.class
    })
    public ResponseEntity<ApiResponse<?>> handleServerError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                        500,
                        "Internal server error",
                        null,
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                        500,
                        "Unexpected server error",
                        null,
                        LocalDateTime.now()
                ));
    }
}
