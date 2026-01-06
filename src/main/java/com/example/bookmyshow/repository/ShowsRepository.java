package com.example.bookmyshow.repository;

import com.example.bookmyshow.models.Theatre.Shows;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShowsRepository extends CrudRepository<Shows, Integer> {
    Optional<Shows> findByShowId(long showId);

    @Modifying
    void deleteByShowId(long showId);

}
