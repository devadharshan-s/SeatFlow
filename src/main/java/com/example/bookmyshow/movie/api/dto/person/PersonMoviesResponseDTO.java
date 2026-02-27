package com.example.bookmyshow.movie.api.dto.person;

import lombok.Data;

import java.util.List;

@Data
public class PersonMoviesResponseDTO {
    private Long personId;
    private List<Long> movieIds;
}




