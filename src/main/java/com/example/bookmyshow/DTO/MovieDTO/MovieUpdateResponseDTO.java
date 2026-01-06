package com.example.bookmyshow.DTO.MovieDTO;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class MovieUpdateResponseDTO {
    private String title;
    private List<GenreDTO> genres;
    private double rating;
    private int runtime;
    private String language;
    private String CBFC;
    private List<MovieCastDTO> cast;
    private Date releaseDate;
}
