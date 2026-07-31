package org.example.bookmyshowbookingservice.booking.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.example.bookmyshowbookingservice.common.exception.DownstreamServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Resilient delegate wrapper around the Feign {@link SeatClient}.
 *
 * <p>Adds Resilience4j {@code @Retry} (with exponential backoff) and {@code @CircuitBreaker}
 * to downstream calls. Since Feign fallbacks swallow exceptions and return {@link ApiResponse}
 * objects with error status codes, this wrapper inspects the returned status and throws
 * {@link DownstreamServiceException} to trigger Resilience4j's exception-driven retry logic.</p>
 *
 * <p><strong>Interview pattern:</strong> Decorator/Delegate pattern — adds resilience behavior
 * around an existing client without modifying the Feign interface (Open/Closed Principle).</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResilientSeatClient {

    private final SeatClient seatClient;

    @Retry(name = "seatService")
    @CircuitBreaker(name = "seatServiceCircuit")
    public ApiResponse<List<Long>> resolveShowSeatIds(Long showId, List<Long> seatIds) {
        ApiResponse<List<Long>> response = seatClient.resolveShowSeatIds(showId, seatIds);
        validateResponse(response, "resolveShowSeatIds");
        return response;
    }

    @Retry(name = "seatService")
    @CircuitBreaker(name = "seatServiceCircuit")
    public ApiResponse<List<Long>> holdSeats(Long ticketId, int holdSeconds, String bookingToken, List<Long> seatIds) {
        ApiResponse<List<Long>> response = seatClient.holdSeats(ticketId, holdSeconds, bookingToken, seatIds);
        validateResponse(response, "holdSeats");
        return response;
    }

    // bookSeats is idempotent (same ticketId re-books safely), so @Retry is safe here
    @Retry(name = "seatService")
    @CircuitBreaker(name = "seatServiceCircuit")
    public ApiResponse<List<Long>> bookSeats(Long ticketId, List<Long> seatIds) {
        ApiResponse<List<Long>> response = seatClient.bookSeats(ticketId, seatIds);
        validateResponse(response, "bookSeats");
        return response;
    }

    @Retry(name = "seatService")
    @CircuitBreaker(name = "seatServiceCircuit")
    public ApiResponse<Boolean> unlockSeats(Long ticketId, List<Long> seatIds) {
        ApiResponse<Boolean> response = seatClient.unlockSeats(ticketId, seatIds);
        validateResponse(response, "unlockSeats");
        return response;
    }

    @Retry(name = "seatService")
    @CircuitBreaker(name = "seatServiceCircuit")
    public ApiResponse<List<Long>> cancelSeats(Long ticketId) {
        ApiResponse<List<Long>> response = seatClient.cancelSeats(ticketId);
        validateResponse(response, "cancelSeats");
        return response;
    }

    @Retry(name = "seatService")
    @CircuitBreaker(name = "seatServiceCircuit")
    public ApiResponse<Boolean> releaseHold(Long ticketId) {
        ApiResponse<Boolean> response = seatClient.releaseHold(ticketId);
        validateResponse(response, "releaseHold");
        return response;
    }

    @Retry(name = "seatService")
    @CircuitBreaker(name = "seatServiceCircuit")
    public ApiResponse<List<Long>> confirmHold(Long ticketId) {
        ApiResponse<List<Long>> response = seatClient.confirmHold(ticketId);
        validateResponse(response, "confirmHold");
        return response;
    }

    /**
     * Bridges Feign fallback semantics with Resilience4j's exception-driven model.
     * Feign fallbacks return ApiResponse with error status codes instead of throwing;
     * this method converts those soft failures into thrown exceptions so @Retry can detect them.
     */
    private void validateResponse(ApiResponse<?> response, String methodName) {
        if (response == null) {
            throw new DownstreamServiceException(503, "Null response from show-service for " + methodName);
        }
        if (response.getStatus() >= 500) {
            log.warn("Downstream failure in {}: status={}, message={}",
                    methodName, response.getStatus(), response.getMessage());
            throw new DownstreamServiceException(response.getStatus(), response.getMessage());
        }
    }
}
