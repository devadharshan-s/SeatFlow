package com.example.bookmyshow.booking.api;

import com.example.bookmyshow.booking.api.dto.TicketDTO;
import com.example.bookmyshow.booking.service.TicketService;
import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.show.api.dto.SeatAvailabilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/selectSeats/{showId}")
    public ResponseEntity<ApiResponse<List<SeatAvailabilityResponse>>> getSeats
            (@PathVariable long showId, @RequestParam(defaultValue = "ALL") String status){

        List<SeatAvailabilityResponse> response = ticketService.getSeats(showId, status);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "✔ Seats fetched Successfully!",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    @DeleteMapping("/getTickets/{showId}")
    public void deleteTicketsByShowId(@PathVariable Long showId){
        ticketService.deleteTicketsByShowId(showId);
    }

    @GetMapping("/validateTicket/{ticketId}")
    public void validateTicket(@PathVariable Long ticketId) {
        ticketService.validateTicketExists(ticketId);
    }
    @PostMapping("/bookTickets")
    public ResponseEntity<ApiResponse<TicketDTO>> bookTicket(@RequestBody TicketDTO ticketDTO) {
        TicketDTO ticket = ticketService.bookTicket(ticketDTO);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "✔ Ticket booked Successfully!",
                        ticket,
                        LocalDateTime.now()
                )
        );
    }

    @DeleteMapping("/deleteBooking")
    public ResponseEntity<ApiResponse<TicketDTO>> deleteBooking(@RequestParam long ticketId) {
        TicketDTO canceledTicket = ticketService.cancelTicket(ticketId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "✔ Ticket cancelled successfully!",
                        canceledTicket,
                        LocalDateTime.now()
                )
        );
    }
}





