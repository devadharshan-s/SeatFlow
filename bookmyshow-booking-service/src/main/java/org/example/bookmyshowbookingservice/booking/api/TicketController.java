package org.example.bookmyshowbookingservice.booking.api;

import lombok.RequiredArgsConstructor;
import org.example.bookmyshowbookingservice.booking.api.dto.TicketDTO;
import org.example.bookmyshowbookingservice.booking.service.TicketService;
import org.example.bookmyshowbookingservice.common.annotation.RateLimit;
import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class TicketController {

        private final TicketService ticketService;

        @DeleteMapping("/getTickets/{showId}")
        public void deleteTicketsByShowId(@PathVariable Long showId) {
                ticketService.deleteTicketsByShowId(showId);
        }

        @GetMapping("/validateTicket/{ticketId}")
        public void validateTicket(@PathVariable Long ticketId) {
                ticketService.validateTicketExists(ticketId);
        }

        @PostMapping("/bookTickets")
        @RateLimit(rate = 3, rateInterval = 60, rateIntervalUnit = TimeUnit.SECONDS)
        public ResponseEntity<ApiResponse<TicketDTO>> bookTicket(@RequestBody TicketDTO ticketDTO) {
                TicketDTO ticket = ticketService.bookTicket(ticketDTO);

                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "✔ Ticket booked Successfully!",
                                                ticket,
                                                LocalDateTime.now()));
        }

        @DeleteMapping("/deleteBooking")
        @RateLimit(rate = 3, rateInterval = 60, rateIntervalUnit = TimeUnit.SECONDS)
        public ResponseEntity<ApiResponse<TicketDTO>> deleteBooking(@RequestParam long ticketId) {
                TicketDTO canceledTicket = ticketService.cancelTicket(ticketId);

                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                200,
                                                "✔ Ticket cancelled successfully!",
                                                canceledTicket,
                                                LocalDateTime.now()));
        }
}
