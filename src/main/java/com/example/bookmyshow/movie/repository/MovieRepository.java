package com.example.bookmyshow.movie.repository;

import com.example.bookmyshow.movie.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findBymovieId(long movieId);

    @Modifying
    void deleteMovieByMovieId(long movieId);
}




