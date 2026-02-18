package com.example.bookmyshow.show.client;

import com.example.bookmyshow.movie.api.dto.MovieResponseDTO;

public interface MovieClient {

    MovieResponseDTO getMovie(Long movieId);
}
