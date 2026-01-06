package com.example.bookmyshow.DTO.MovieDTO;

import lombok.Data;

import java.util.List;

@Data
public class MovieDTO {

    private String title;
    private List<GenreDTO> genres;
    private int runtime;
    private String language;
    private String CBFC;
    private List<MovieCastDTO> cast;
}
