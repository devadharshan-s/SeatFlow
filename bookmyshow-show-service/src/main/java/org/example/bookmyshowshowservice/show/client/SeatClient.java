package org.example.bookmyshowshowservice.show.client;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.show.client.dto.SeatResponseDTO;
import org.example.bookmyshowshowservice.show.client.impl.SeatClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "bookmyshow-theatre-service", contextId = "showSeatClient", fallback = SeatClientFallback.class)
public interface SeatClient {

    @PostMapping("/getSeats")
    ApiResponse<Map<Long, SeatResponseDTO>> getSeats(@RequestBody List<Long> seatIds);

    @GetMapping("/getSeatsByScreen/{screenId}")
    ApiResponse<List<SeatResponseDTO>> getSeatsByScreen(@PathVariable("screenId") Long screenId);
}
