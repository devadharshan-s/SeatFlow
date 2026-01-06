package com.example.bookmyshow.DTO.MovieDTO;

import lombok.Data;

import java.util.Set;

@Data
public class MovieResponseDTO {
    private long movieId;
    private String title;
    private Set<Long> theaterIds;
}
