package com.example.bookmyshow.movie.repository;

import com.example.bookmyshow.movie.model.MovieCast;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieCastRepository extends JpaRepository<MovieCast, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MovieCast mc WHERE mc.movieCastId IN :ids")
    void deleteMovieCastByMovieCastId(@Param("ids") List<Long> movieCastId);
}




