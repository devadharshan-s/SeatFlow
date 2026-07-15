package org.example.bookmyshowshowservice.show.client.impl;

import org.example.bookmyshowshowservice.common.exception.TicketNotFoundException;
import org.example.bookmyshowshowservice.show.client.TicketClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component("ticketClient")
public class TicketRestClient implements TicketClient {

    private final RestClient restClient;

    public TicketRestClient(@Qualifier("bookingRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void deleteTickets(Long showId) {
        restClient.delete()
                .uri("/getTickets/{showId}", showId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void validateTicketExists(Long ticketId) {
        try {
            restClient.get()
                    .uri("/validateTicket/{ticketId}", ticketId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new TicketNotFoundException("Ticket not found, Check ticket Id!");
        }
    }
}
