package org.example.bookmyshowshowservice.show.client.impl;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.common.exception.ScreenNotFoundException;
import org.example.bookmyshowshowservice.show.client.ScreenClient;
import org.example.bookmyshowshowservice.show.client.dto.ScreenResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("screenClient")
public class ScreenRestClient implements ScreenClient {

    private final RestClient restClient;

    public ScreenRestClient(@Qualifier("theatreRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ScreenResponseDTO getScreen(long screenId) {
        ApiResponse<ScreenResponseDTO> response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getScreen")
                        .queryParam("screenId", screenId)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<ScreenResponseDTO>>() {});

        if (response == null || response.getData() == null) {
            throw new ScreenNotFoundException("Screen not found for id: " + screenId);
        }

        return response.getData();
    }
}
