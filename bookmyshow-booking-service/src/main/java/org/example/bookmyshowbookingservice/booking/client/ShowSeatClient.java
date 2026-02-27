package org.example.bookmyshowbookingservice.booking.client;

import org.example.bookmyshowbookingservice.booking.api.dto.SeatAvailabilityResponse;
import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.example.bookmyshowbookingservice.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "showSeatAvailabilityClient", url = "${services.show.base-url}", configuration = FeignClientConfig.class)
public interface ShowSeatClient {

    @GetMapping("/getShowSeats/{showId}")
    ApiResponse<List<SeatAvailabilityResponse>> seats(@PathVariable("showId") Long showId, @RequestParam("status") String status);
}
