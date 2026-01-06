package com.example.bookmyshow.models.Movie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Genre_Id")
    private int genreId;

    private String name;
}
