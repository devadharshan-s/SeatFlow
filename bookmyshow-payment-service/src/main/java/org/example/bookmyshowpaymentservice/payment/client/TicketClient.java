package org.example.bookmyshowpaymentservice.payment.client;

import org.example.bookmyshowpaymentservice.payment.client.impl.TicketClientFallback;
import org.example.bookmyshowpaymentservice.payment.config.TicketClientFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "bookmyshow-booking-service",
        configuration = TicketClientFeignConfig.class,
        fallback = TicketClientFallback.class
)
public interface TicketClient {

    @GetMapping("/validateTicket/{ticketId}")
    void validateTicketExists(@PathVariable("ticketId") Long ticketId);
}
