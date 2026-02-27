package com.example.bookmyshow.movie.api.dto;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class MovieUpdateDTO {
    private String title;
    private List<String> genres;
    private Double rating;
    private Integer runtime;
    private String language;
    private String CBFC;
    private List<MovieCastDTO> cast;
    private Date releaseDate;
}




