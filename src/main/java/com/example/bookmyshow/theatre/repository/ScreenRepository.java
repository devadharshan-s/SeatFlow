package com.example.bookmyshow.theatre.repository;

import com.example.bookmyshow.theatre.model.Screen;
import com.example.bookmyshow.theatre.model.Theatre;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenRepository extends CrudRepository<Screen, Long> {
    Optional<Screen> getScreensByScreenId(long screenId);

    Optional<Screen> findByScreenId(long screenId);

    Optional<List<Screen>> findScreensByTheatre(Theatre theatre);
}




