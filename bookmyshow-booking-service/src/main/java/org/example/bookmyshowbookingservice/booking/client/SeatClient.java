package org.example.bookmyshowbookingservice.booking.client;

import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.example.bookmyshowbookingservice.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "showSeatClient", url = "${services.show.base-url}", configuration = FeignClientConfig.class)
public interface SeatClient {

    @PostMapping("/shows/{showId}/resolve-seat-ids")
    ApiResponse<List<Long>> resolveShowSeatIds(@PathVariable("showId") Long showId, @RequestBody List<Long> seatIds);

    @PostMapping("/lockSeats/{seconds}")
    ApiResponse<List<Long>> lockSeats(@PathVariable("seconds") int seconds, @RequestBody List<Long> seatIds);

    @PostMapping("/bookSeats/{ticketId}")
    ApiResponse<List<Long>> bookSeats(@PathVariable("ticketId") long ticketId, @RequestBody List<Long> seatIds);

    @PostMapping("/unlockSeats/{ticketId}")
    ApiResponse<Boolean> unlockSeats(@PathVariable("ticketId") Long ticketId, @RequestBody List<Long> seatIds);

    @DeleteMapping("/cancelSeats/{ticketId}")
    ApiResponse<List<Long>> cancelSeats(@PathVariable("ticketId") Long ticketId);
}
