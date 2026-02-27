package org.example.bookmyshowshowservice.show.client.impl;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.show.client.SeatClient;
import org.example.bookmyshowshowservice.show.exception.SeatNotFoundException;
import org.example.bookmyshowshowservice.show.client.dto.SeatResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component("showModuleSeatRestClient")
@RequiredArgsConstructor
public class SeatRestClient implements SeatClient {

    private final RestClient restClient;

    @Override
    public Map<Long, SeatResponseDTO> getSeats(List<Long> seatIds) {
        ApiResponse<Map<Long, SeatResponseDTO>> response =
                 restClient.post()
                .uri("/getSeats")
                .body(seatIds)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<Map<Long, SeatResponseDTO>>>() {});

        if(response == null || response.getData() == null) {
            throw new SeatNotFoundException("Seats not found for ids: " + seatIds);
        }
        return response.getData();
    }

    @Override
    public List<SeatResponseDTO> getSeatsByScreen(Long screenId) {
        ApiResponse<List<SeatResponseDTO>> response = restClient.get()
                .uri("/getSeatsByScreen/{screenId}", screenId)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<List<SeatResponseDTO>>>() {});

        if(response == null || response.getData() == null) {
            throw new SeatNotFoundException("Seats not found for screen id: " + screenId);
        }

        return response.getData();
    }
}








