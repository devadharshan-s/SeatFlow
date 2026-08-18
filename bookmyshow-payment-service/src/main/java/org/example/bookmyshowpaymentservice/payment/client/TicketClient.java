package org.example.bookmyshowpaymentservice.payment.client;

import org.example.bookmyshowpaymentservice.payment.client.impl.TicketClientFallback;
import org.example.bookmyshowpaymentservice.payment.config.TicketClientFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "bookmyshow-booking-service",
        configuration = TicketClientFeignConfig.class,
        fallback = TicketClientFallback.class
)
public interface TicketClient {

    @PostMapping("/confirmBooking")
    void confirmBooking(@RequestParam String bookingToken);
    
}
