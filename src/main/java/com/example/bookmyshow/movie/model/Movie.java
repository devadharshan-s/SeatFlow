package com.example.bookmyshow.movie.model;

import com.example.bookmyshow.constants.AuditEntity;
import com.example.bookmyshow.movie.model.Genre.Genre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Movie extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Movie_id")
    private long movieId;

    private String title;

//    Movie, Genre belong to the same modules so Genre reference is ok
    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "Movie_id"),
            inverseJoinColumns = @JoinColumn(name = "Genre_id")
    )
    private List<Genre> genres;


    private double rating;

    private int runtime;

    @Column(name = "Release_Date")
    private Date releaseDate;

    private String CBFC;

    private String language;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieCast> cast = new ArrayList<>();
}




