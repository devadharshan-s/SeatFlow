package org.example.bookmyshowmovieservice.movie.client;

import org.example.bookmyshowmovieservice.common.dto.ApiResponse;
import org.example.bookmyshowmovieservice.movie.client.impl.ShowClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "bookmyshow-show-service", fallback = ShowClientFallback.class)
public interface ShowClient {

    @GetMapping("/getAllShows")
    ApiResponse<List<Long>> getAllShows(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    );
}
