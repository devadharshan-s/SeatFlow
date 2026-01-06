package com.example.bookmyshow.repository;

import com.example.bookmyshow.models.Theatre.Seat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends CrudRepository<Seat, Long> {
    List<Seat> findSeatByScreen_ScreenId(long screenId);

    @Modifying
    @Transactional
    void deleteSeatByScreen_ScreenId(long ScreenId);

    @Modifying
    @Transactional
    void deleteSeatBySeatId(long seatId);
}
