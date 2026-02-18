package com.example.bookmyshow.show.repository;

import com.example.bookmyshow.show.model.Shows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowsRepository extends JpaRepository<Shows, Long> {
    Optional<Shows> findByShowId(long showId);

    @Modifying
    void deleteByShowId(long showId);

    Optional<List<Shows>> findShowsByScreenId(long screenId);

    Optional<Shows> findByScreenIdAndStartTimeLessThanAndEndTimeGreaterThan(
            long screenId,
            LocalDateTime newEnd,
            LocalDateTime newStart
    );
}






