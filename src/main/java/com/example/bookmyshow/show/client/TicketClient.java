package com.example.bookmyshow.show.client;

public interface TicketClient {

    void deleteTickets(Long showId);

    void validateTicketExists(Long ticketId);
}
