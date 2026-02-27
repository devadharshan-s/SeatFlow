package org.example.bookmyshowtheatreservice.theatre.repository;

import org.example.bookmyshowtheatreservice.theatre.model.Screen;
import org.example.bookmyshowtheatreservice.theatre.model.Theatre;
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





