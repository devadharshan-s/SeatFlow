package org.example.bookmyshowshowservice.show.client;

import org.example.bookmyshowshowservice.show.client.dto.TheatreDTO;

public interface TheatreClient {
    TheatreDTO getTheatre(Long theatreId);
}


