package org.example.bookmyshowbookingservice.common.exception;

import feign.FeignException;
import org.example.bookmyshowbookingservice.booking.exception.BookingFailedException;
import org.example.bookmyshowbookingservice.booking.exception.InvalidShowIdException;
import org.example.bookmyshowbookingservice.booking.exception.TicketCancellationException;
import org.example.bookmyshowbookingservice.booking.exception.TicketDeletionException;
import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleTicketNotFound(TicketNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidShowIdException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidShowId(InvalidShowIdException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({BookingFailedException.class, TicketCancellationException.class})
    public ResponseEntity<ApiResponse<Void>> handleBookingErrors(ServiceException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(TicketDeletionException.class)
    public ResponseEntity<ApiResponse<Void>> handleTicketDeletion(TicketDeletionException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDownstream(DownstreamServiceException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode());
        if (status == null) {
            status = HttpStatus.BAD_GATEWAY;
        }
        return build(status, ex.getMessage());
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
