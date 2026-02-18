package com.example.bookmyshow.movie.repository;

import com.example.bookmyshow.movie.model.Genre.Genre;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends CrudRepository<Genre, Integer> {

    Genre findByName(String name);

    @Query("SELECT g FROM Genre g WHERE g.name IN :names")
    List<Genre> findByNameIn(@Param("names") List<String> names);
}




