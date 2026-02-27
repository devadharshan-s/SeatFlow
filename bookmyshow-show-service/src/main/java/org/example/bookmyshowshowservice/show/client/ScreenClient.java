package org.example.bookmyshowshowservice.show.client;

import org.example.bookmyshowshowservice.show.client.dto.ScreenResponseDTO;

public interface ScreenClient {

    public ScreenResponseDTO getScreen(long screenId);
}

