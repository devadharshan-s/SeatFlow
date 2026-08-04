package org.example.bookmyshowshowservice.show.api;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.show.api.dto.SeatAvailabilityResponse;
import org.example.bookmyshowshowservice.show.service.ShowSeatService;
import org.example.bookmyshowshowservice.show.service.SeatHoldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ShowSeatController {

        private final ShowSeatService showSeatService;
        private final SeatHoldService seatHoldService;

        @PostMapping("/shows/{showId}/resolve-seat-ids")
        public ResponseEntity<ApiResponse<List<Long>>> resolveShowSeatIds(@PathVariable Long showId,
                        @RequestBody List<Long> seatIds) {
                List<Long> resolvedIds = showSeatService.resolveShowSeatIds(showId, seatIds);
                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "Seats resolved successfully",
                                                resolvedIds,
                                                LocalDateTime.now()));
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
                                                LocalDateTime.now()));
        }

        @GetMapping("/getShowSeatsByTicket/{ticketId}")
        public ResponseEntity<ApiResponse<List<Long>>> getShowSeatsByTicketId(@PathVariable Long ticketId) {
                List<Long> seatIds = showSeatService.getShowSeatsByTicketId(ticketId);
                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "Seats retrieved successfully for ticket",
                                                seatIds,
                                                LocalDateTime.now()));
        }


        @PostMapping("/bookSeats/{bookingToken}")
        public ResponseEntity<ApiResponse<List<Long>>> bookSeats(@PathVariable String bookingToken,
                        @RequestBody List<Long> showSeatIds) {

                List<Long> bookSeats = showSeatService.bookSeats(showSeatIds, 0L);

                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "Seats booked successfully",
                                                bookSeats,
                                                LocalDateTime.now()));
        }

        @PostMapping("/unlockSeats/{ticketId}")
        public ResponseEntity<ApiResponse<Boolean>> unlockSeats(@PathVariable Long ticketId,
                        @RequestBody List<Long> showSeatIds) {
                Boolean unlocked = showSeatService.unlockSeats(ticketId, showSeatIds);
                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "Seats unlocked successfully",
                                                unlocked,
                                                LocalDateTime.now()));
        }

        @DeleteMapping("/cancelSeats/{ticketId}")
        public ResponseEntity<ApiResponse<List<Long>>> cancelSeats(@PathVariable Long ticketId) {
                List<Long> unbookedSeats = showSeatService.cancelSeats(ticketId);

                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "Seats cancelled successfully",
                                                unbookedSeats,
                                                LocalDateTime.now()));
        }

        @PostMapping("/show-seat/holdSeats")
        public ResponseEntity<ApiResponse<List<Long>>> holdSeats(
                        @RequestParam(value = "bookingToken", required = false) String bookingTokenParam,
                        @RequestParam(value = "ticketId", required = false) String ticketIdParam,
                        @RequestParam("holdSeconds") int holdSeconds,
                        @RequestBody List<Long> showSeatIds) {

                String token = (bookingTokenParam != null && !bookingTokenParam.isBlank()) ? bookingTokenParam : ticketIdParam;
                List<Long> response = showSeatService.holdSeats(token, showSeatIds, holdSeconds);
                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "Seats held successfully",
                                                response,
                                                LocalDateTime.now()));
        }

        @PostMapping("/show-seat/releaseHold")
        public ResponseEntity<ApiResponse<Boolean>> releaseHold(
                        @RequestParam(value = "bookingToken", required = false) String bookingTokenParam,
                        @RequestParam(value = "ticketId", required = false) String ticketIdParam) {

                String token = (bookingTokenParam != null && !bookingTokenParam.isBlank()) ? bookingTokenParam : ticketIdParam;
                Boolean released = showSeatService.releaseHold(token);
                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "Holds released successfully",
                                                released,
                                                LocalDateTime.now()));
        }

        @PostMapping("/show-seat/confirmHold")
        public ResponseEntity<ApiResponse<List<Long>>> confirmHold(
                        @RequestParam(value = "bookingToken", required = false) String bookingTokenParam,
                        @RequestParam(value = "ticketId", required = false) String ticketIdParam) {

                String token = (bookingTokenParam != null && !bookingTokenParam.isBlank()) ? bookingTokenParam : ticketIdParam;
                List<Long> seatIds = showSeatService.confirmHold(token);
                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "Holds confirmed successfully",
                                                seatIds,
                                                LocalDateTime.now()));
        }

        @PostMapping("/shows/{showId}/hold-seats")
        public ResponseEntity<ApiResponse<List<Long>>> holdAndResolveSeats(
                        @PathVariable Long showId,
                        @RequestParam("bookingToken") String bookingToken,
                        @RequestParam(value = "holdSeconds", defaultValue = "300") int holdSeconds,
                        @RequestBody List<Long> seatIds) {

                List<Long> showSeatIds = showSeatService.resolveShowSeatIds(showId, seatIds);
                List<Long> response = showSeatService.holdSeats(bookingToken, showSeatIds, holdSeconds);
                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "Seats resolved and held successfully",
                                                response,
                                                LocalDateTime.now()));
        }
}
