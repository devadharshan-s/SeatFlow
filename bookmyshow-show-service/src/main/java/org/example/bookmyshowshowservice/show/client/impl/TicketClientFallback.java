package org.example.bookmyshowshowservice.show.client.impl;

import org.example.bookmyshowshowservice.show.client.TicketClient;
import org.springframework.stereotype.Component;

@Component("showModuleTicketClientFallback")
public class TicketClientFallback implements TicketClient {

    @Override
    public void deleteTickets(Long showId) {

        // Fallback execution when booking-service is unavailable
        System.err.println("Fallback: Unable to trigger deleteTickets for showId: " + showId);

    }

    @Override
    public void validateTicketExists(Long ticketId) {

        // Fallback execution when booking-service is unavailable
        System.err.println("Fallback: Unable to validate ticket existence for ticketId: " + ticketId);

    }
}
