package org.example.bookmyshowpaymentservice.payment.client;

import org.example.bookmyshowpaymentservice.payment.config.TicketClientFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "bookingClient",
        url = "${services.booking.base-url}",
        configuration = TicketClientFeignConfig.class
)
public interface TicketClient {

    @GetMapping("/validateTicket/{ticketId}")
    void validateTicketExists(@PathVariable("ticketId") Long ticketId);
}

