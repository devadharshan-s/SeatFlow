package org.example.bookmyshowshowservice.show.client;

import org.example.bookmyshowshowservice.show.client.impl.TicketClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "bookmyshow-booking-service", contextId = "showBookingTicketClient", fallback = TicketClientFallback.class)
public interface TicketClient {

    @DeleteMapping("/getTickets/{showId}")
    void deleteTickets(@PathVariable("showId") Long showId);

    @GetMapping("/validateTicket/{ticketId}")
    void validateTicketExists(@PathVariable("ticketId") Long ticketId);
}
