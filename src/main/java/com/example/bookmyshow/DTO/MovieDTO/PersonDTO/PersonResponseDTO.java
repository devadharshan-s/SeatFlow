package com.example.bookmyshow.DTO.MovieDTO.PersonDTO;

import lombok.Data;

import java.util.List;

@Data
public class PersonResponseDTO {

    private long personId;
    private String name;
    private int age;
    List<String> movies;
}
