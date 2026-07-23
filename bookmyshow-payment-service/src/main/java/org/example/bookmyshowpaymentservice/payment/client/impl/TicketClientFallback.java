package org.example.bookmyshowpaymentservice.payment.client.impl;

import org.example.bookmyshowpaymentservice.payment.client.TicketClient;
import org.springframework.stereotype.Component;

@Component
public class TicketClientFallback implements TicketClient {

    @Override
    public void validateTicketExists(Long ticketId) {

        // Fallback execution when booking-service is unavailable
        System.err.println("Fallback: Unable to validate ticket existence for ticketId: " + ticketId);

    }
}
