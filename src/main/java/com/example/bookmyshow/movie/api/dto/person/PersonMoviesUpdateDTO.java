package com.example.bookmyshow.movie.api.dto.person;

import lombok.Data;

import java.util.List;

@Data
public class PersonMoviesUpdateDTO {
    private List<Long> movieIds;
}




