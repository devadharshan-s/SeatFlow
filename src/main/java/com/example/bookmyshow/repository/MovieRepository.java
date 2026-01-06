package com.example.bookmyshow.repository;

import com.example.bookmyshow.models.Movie.Movie;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Integer> {
    Optional<Movie> findBymovieId(long movieId);

    @Modifying
    void deleteMovieByMovieId(long movieId);
}
