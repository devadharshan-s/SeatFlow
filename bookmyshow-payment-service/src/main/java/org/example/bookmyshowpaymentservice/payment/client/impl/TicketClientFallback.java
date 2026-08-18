package org.example.bookmyshowpaymentservice.payment.client.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.bookmyshowpaymentservice.payment.client.TicketClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TicketClientFallback implements TicketClient {

    @Override
    public void confirmBooking(String bookingToken) {

        // Fallback: booking-service is unavailable — booking confirmation lost; manual reconciliation may be needed
        log.error("Fallback: Unable to confirm booking for bookingToken: {}. Payment succeeded but booking not confirmed — investigate!", bookingToken);

    }
}
