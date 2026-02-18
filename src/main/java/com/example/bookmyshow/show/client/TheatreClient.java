package com.example.bookmyshow.show.client;

import com.example.bookmyshow.theatre.api.dto.TheatreDTO;

public interface TheatreClient {
    TheatreDTO getTheatre(Long theatreId);
}

