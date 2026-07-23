package org.example.bookmyshowshowservice.show.client;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.show.client.dto.ScreenResponseDTO;
import org.example.bookmyshowshowservice.show.client.impl.ScreenClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bookmyshow-theatre-service", contextId = "showScreenClient", fallback = ScreenClientFallback.class)
public interface ScreenClient {

    @GetMapping("/getScreen")
    ApiResponse<ScreenResponseDTO> getScreen(@RequestParam("screenId") long screenId);
}
