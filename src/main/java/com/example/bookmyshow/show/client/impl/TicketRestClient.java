package com.example.bookmyshow.show.client.impl;

import com.example.bookmyshow.exception.TicketNotFoundException;
import com.example.bookmyshow.show.client.TicketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class TicketRestClient implements TicketClient {

    private final RestClient restClient;

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
