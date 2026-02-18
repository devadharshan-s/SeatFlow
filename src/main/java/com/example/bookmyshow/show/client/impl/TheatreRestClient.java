package com.example.bookmyshow.show.client.impl;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.exception.TheatreNotFoundException;
import com.example.bookmyshow.show.client.TheatreClient;
import com.example.bookmyshow.theatre.api.dto.TheatreDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class TheatreRestClient implements TheatreClient {

    private final RestClient restClient;

    @Override
    public TheatreDTO getTheatre(Long theatreId) {
        ApiResponse<TheatreDTO> response = restClient.get()
                .uri("/getTheatre/{theatreId}", theatreId)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<TheatreDTO>>() {});

        if (response == null || response.getData() == null) {
            throw new TheatreNotFoundException("Theatre not found for id: " + theatreId);
        }

        return response.getData();
    }
}



