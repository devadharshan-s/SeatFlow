package org.example.bookmyshowbookingservice.booking.repository;

import java.util.Optional;

import org.example.bookmyshowbookingservice.booking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    Optional<Reservation> findByBookingToken(String bookingToken);
    
}
