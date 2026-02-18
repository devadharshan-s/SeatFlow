package com.example.bookmyshow.show.client.impl;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.exception.ScreenNotFoundException;
import com.example.bookmyshow.show.client.ScreenClient;
import com.example.bookmyshow.theatre.api.dto.ScreenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ScreenRestClient implements ScreenClient {

    private final RestClient restClient;

    @Override
    public ScreenResponseDTO getScreen(long screenId) {
        ApiResponse<ScreenResponseDTO> response = restClient.get()
                .uri("/getScreen/{screenId}", screenId)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<ScreenResponseDTO>>() {});

        if (response == null || response.getData() == null) {
            throw new ScreenNotFoundException("Screen not found for id: " + screenId);
        }

        return response.getData();
    }
}
