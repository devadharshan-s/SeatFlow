package com.example.bookmyshow.repository;

import com.example.bookmyshow.models.Theatre.Theatre;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TheatreRepository extends CrudRepository<Theatre, Integer> {
    Optional<Theatre> findByTheatreId(long theatreId);

    @Modifying
    @Transactional
    void deleteByTheatreId(long theatreId);
}
