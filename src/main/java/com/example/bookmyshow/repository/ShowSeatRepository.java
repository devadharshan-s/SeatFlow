package com.example.bookmyshow.repository;

import com.example.bookmyshow.models.Theatre.ShowSeat;
import com.example.bookmyshow.models.Theatre.Shows;
import com.example.bookmyshow.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Integer> {

    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show = :show AND ss.seat.seatId IN :seatIds")
    List<ShowSeat> findShowSeats(@Param("show") Shows show, @Param("seatIds") List<Long> seatIds);

    List<ShowSeat> findByShow(Shows show);

    Optional<List<ShowSeat>> findByTicket(Ticket ticket);
}
