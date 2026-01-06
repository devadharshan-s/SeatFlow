package com.example.bookmyshow.repository;

import com.example.bookmyshow.models.Movie.Genre;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends CrudRepository<Genre, Integer> {

    Genre findByName(String name);

}
