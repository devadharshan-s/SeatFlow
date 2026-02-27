package org.example.bookmyshowmovieservice.movie.model;

import org.example.bookmyshowmovieservice.movie.model.Genre.Genre;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movie")
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "title")
    private String title;

    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    @Column(name = "rating")
    private double rating;

    @Column(name = "runtime")
    private int runtime;

    @Column(name = "release_date")
    private Date releaseDate;

    @Column(name = "cbfc")
    private String CBFC;

    @Column(name = "language")
    private String language;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieCast> cast = new ArrayList<>();
}

