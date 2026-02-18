package com.example.bookmyshow.show.client;

import com.example.bookmyshow.theatre.api.dto.ScreenResponseDTO;

public interface ScreenClient {

    public ScreenResponseDTO getScreen(long screenId);
}
