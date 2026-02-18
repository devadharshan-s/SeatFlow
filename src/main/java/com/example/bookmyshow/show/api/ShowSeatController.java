package com.example.bookmyshow.show.api;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.show.api.dto.SeatAvailabilityResponse;
import com.example.bookmyshow.show.service.LockService;
import com.example.bookmyshow.show.service.ShowSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShowSeatController {

    private final ShowSeatService showSeatService;
    private final LockService lockService;

    @PostMapping("/shows/{showId}/resolve-seat-ids")
    public ResponseEntity<ApiResponse<List<Long>>> resolveShowSeatIds(@PathVariable Long showId, @RequestBody List<Long> seatIds) {
        List<Long> resolvedIds = showSeatService.resolveShowSeatIds(showId, seatIds);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Seats resolved successfully",
                        resolvedIds,
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/getShowSeats/{showId}")
    public ResponseEntity<ApiResponse<List<SeatAvailabilityResponse>>> getShowSeatsByStatus(
            @PathVariable Long showId,
            @RequestParam(defaultValue = "ALL") String status) {

        List<SeatAvailabilityResponse> response = showSeatService.getShowSeats(showId, status);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Seats retrieved successfully",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/getShowSeatsByTicket/{ticketId}")
    public ResponseEntity<ApiResponse<List<Long>>> getShowSeatsByTicketId(@PathVariable Long ticketId) {
        List<Long> seatIds = showSeatService.getShowSeatsByTicketId(ticketId);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Seats retrieved successfully for ticket",
                        seatIds,
                        LocalDateTime.now()
                )
        );
    }

    @PostMapping("/lockSeats/{seconds}")
    public ResponseEntity<ApiResponse<List<Long>>> lockSeats(@RequestBody List<Long> showSeatIds, @PathVariable int seconds) {
        List<Long> response = lockService.lockSeats(showSeatIds, seconds);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Seats locked successfully",
                        response,
                        LocalDateTime.now()
                ));
    }

    @PostMapping("/bookSeats/{ticketId}")
    public ResponseEntity<ApiResponse<List<Long>>> bookSeats(@PathVariable Long ticketId, @RequestBody List<Long> showSeatIds) {

        List<Long> bookSeats = showSeatService.bookSeats(showSeatIds, ticketId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Seats booked successfully",
                        bookSeats,
                        LocalDateTime.now()
                )
        );
    }

    @PostMapping("/unlockSeats/{ticketId}")
    public ResponseEntity<ApiResponse<Boolean>> unlockSeats(@PathVariable Long ticketId, @RequestBody List<Long> showSeatIds) {
        Boolean unlocked = showSeatService.unlockSeats(ticketId, showSeatIds);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Seats unlocked successfully",
                        unlocked,
                        LocalDateTime.now()
                )
        );
    }

    @DeleteMapping("/cancelSeats/{ticketId}")
    public ResponseEntity<ApiResponse<List<Long>>> cancelSeats(@PathVariable Long ticketId) {
        List<Long> unbookedSeats = showSeatService.cancelSeats(ticketId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Seats cancelled successfully",
                        unbookedSeats,
                        LocalDateTime.now()
                )
        );
    }
}
