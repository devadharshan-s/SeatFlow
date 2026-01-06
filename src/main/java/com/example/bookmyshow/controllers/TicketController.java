package com.example.bookmyshow.controllers;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.TheatreDTO.SeatDTO.SeatAvailabilityDTO;
import com.example.bookmyshow.DTO.TicketDTO;
import com.example.bookmyshow.services.ShowSeatService;
import com.example.bookmyshow.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final ShowSeatService showSeatService;

    @GetMapping("/selectSeats/{showId}")
    public ResponseEntity<ApiResponse<List<SeatAvailabilityDTO>>> getSeats
            (@PathVariable long showId, @RequestParam(defaultValue = "ALL") String status){
        return showSeatService.getShowSeats(showId, status);
    }

    @PostMapping("/bookTickets")
    public ResponseEntity<ApiResponse<TicketDTO>> bookTicket(@RequestBody TicketDTO ticketDTO) {
        return ticketService.bookTicket(ticketDTO);
    }

    @DeleteMapping("/deleteBooking")
    public ResponseEntity<ApiResponse<TicketDTO>> deleteBooking(@RequestParam long ticketId) {
        return ticketService.cancelTicket(ticketId);
    }
}
