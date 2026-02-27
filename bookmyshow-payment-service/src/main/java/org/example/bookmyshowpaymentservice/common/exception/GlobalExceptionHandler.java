package org.example.bookmyshowpaymentservice.common.exception;

import org.example.bookmyshowpaymentservice.common.dto.ApiResponse;
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

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDownstream(DownstreamServiceException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode());
        if (status == null) {
            status = HttpStatus.BAD_GATEWAY;
        }
        return build(status, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(IllegalStateException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
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

