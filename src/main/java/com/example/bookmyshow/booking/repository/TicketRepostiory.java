package com.example.bookmyshow.booking.repository;

import com.example.bookmyshow.booking.model.Ticket;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepostiory extends CrudRepository<Ticket, Long> {

    Optional<List<Ticket>> findAllByShowId(Long showId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Ticket t WHERE t.ticketId IN :ids")
    void deleteTickets(@Param("ids") List<Long> tickets);

    List<Ticket> findByShowId(Long showId);

    @Modifying
    @Transactional
    void deleteByShowId(Long showId);
}





