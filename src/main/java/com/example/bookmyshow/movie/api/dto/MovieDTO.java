package com.example.bookmyshow.movie.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class MovieDTO {

    private String title;
    private List<String> genres;
    private int runtime;
    private String language;
    private String CBFC;
    private List<MovieCastDTO> cast;
}




