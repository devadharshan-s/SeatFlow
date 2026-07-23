package org.example.bookmyshowbookingservice.booking.client;

import org.example.bookmyshowbookingservice.booking.client.impl.ShowClientFallback;
import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.example.bookmyshowbookingservice.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bookmyshow-show-service", configuration = FeignClientConfig.class, fallback = ShowClientFallback.class)
public interface ShowClient {

    @GetMapping("/getShowById")
    ApiResponse<Object> getShowById(@RequestParam("showId") Long showId);
}
