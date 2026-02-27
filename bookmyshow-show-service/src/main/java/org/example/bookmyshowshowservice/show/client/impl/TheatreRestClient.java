package org.example.bookmyshowshowservice.show.client.impl;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.common.exception.TheatreNotFoundException;
import org.example.bookmyshowshowservice.show.client.TheatreClient;
import org.example.bookmyshowshowservice.show.client.dto.TheatreDTO;
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




