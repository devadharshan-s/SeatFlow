package com.example.bookmyshow.theatre.repository;

import com.example.bookmyshow.theatre.model.Seat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends CrudRepository<Seat, Long> {
    Optional<List<Seat>> findSeatByScreen_ScreenId(long screenId);

    @Query("SELECT s FROM Seat s WHERE s.seatId IN :seatIds")
    List<Seat> findSeatsBySeatIds(List<Long> seatIds);

    @Modifying
    @Transactional
    void deleteSeatByScreen_ScreenId(long ScreenId);

    @Modifying
    @Transactional
    void deleteSeatBySeatId(long seatId);
}




