package com.example.bookmyshow.movie.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class MovieResponseDTO {

    private Long movieId;
    private String title;
    private List<String> genres;
    private int runtime;
    private String language;
    private String CBFC;
    private List<MovieCastDTO> cast;
}
