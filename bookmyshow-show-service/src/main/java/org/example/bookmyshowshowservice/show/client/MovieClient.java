package org.example.bookmyshowshowservice.show.client;

import org.example.bookmyshowshowservice.show.client.dto.MovieResponseDTO;

public interface MovieClient {

    MovieResponseDTO getMovie(Long movieId);
}

