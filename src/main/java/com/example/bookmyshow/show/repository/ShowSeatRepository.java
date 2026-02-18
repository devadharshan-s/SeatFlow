package com.example.bookmyshow.show.repository;

import com.example.bookmyshow.show.model.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

//    Get the seats allocated for the show that are not booked yet.
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.showId = :showId AND ss.seatId IN :seatIds AND ss.isBooked = false")
    List<ShowSeat> findShowSeats(@Param("showId") Long showId, @Param("seatIds") List<Long> seatIds);

    @Query("SELECT ss FROM ShowSeat ss WHERE ss.showSeatId IN :showSeatIds")
    List<ShowSeat> findShowSeatByShowSeatIdIs(List<Long> showSeatIds);

    List<ShowSeat> findShowSeatsByTicketId(Long ticketId);

    List<ShowSeat> findShowSeatsByShow_ShowId(Long showId);

}
