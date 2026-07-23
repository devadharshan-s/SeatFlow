package org.example.bookmyshowshowservice.show.client;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.show.client.dto.TheatreDTO;
import org.example.bookmyshowshowservice.show.client.impl.TheatreClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "bookmyshow-theatre-service", contextId = "showTheatreClient", fallback = TheatreClientFallback.class)
public interface TheatreClient {

    @GetMapping("/getTheatre/{theatreId}")
    ApiResponse<TheatreDTO> getTheatre(@PathVariable("theatreId") Long theatreId);
}
