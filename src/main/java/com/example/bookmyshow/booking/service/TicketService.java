package com.example.bookmyshow.booking.service;

import com.example.bookmyshow.booking.api.dto.TicketDTO;
import com.example.bookmyshow.booking.client.SeatClient;
import com.example.bookmyshow.booking.client.ShowClient;
import com.example.bookmyshow.booking.client.ShowSeatClient;
import com.example.bookmyshow.booking.client.UserClient;
import com.example.bookmyshow.booking.exception.BookingFailedException;
import com.example.bookmyshow.booking.exception.InvalidShowIdException;
import com.example.bookmyshow.booking.exception.TicketDeletionException;
import com.example.bookmyshow.booking.model.Ticket;
import com.example.bookmyshow.booking.repository.TicketRepostiory;
import com.example.bookmyshow.exception.TicketNotFoundException;
import com.example.bookmyshow.show.api.dto.SeatAvailabilityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepostiory ticketRepostiory;
    private final ShowClient showClient;
    private final SeatClient seatClient;
    private final UserClient userClient;
    private final ShowSeatClient showSeatClient;
    private final ModelMapper modelMapper;

    public static Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Jwt) auth.getPrincipal();
    }

    @Transactional
    public List<SeatAvailabilityResponse> getSeats(Long showId, String status){
        return showSeatClient.seats(showId, status);
    }

    @Transactional(readOnly = true)
    public void validateTicketExists(Long ticketId) {
        if (ticketId == null || ticketId <= 0 || ticketRepostiory.findById(ticketId).isEmpty()) {
            throw new TicketNotFoundException("Ticket not found, Check ticket Id!");
        }
    }

    @Transactional
    public void deleteTicketsByShowId(Long showId) {
        if (showId == null || showId <= 0) {
            throw new InvalidShowIdException(showId);
        }

        List<Ticket> tickets = ticketRepostiory.findByShowId(showId);

        if (tickets == null || tickets.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for showId: " + showId);
        }

        try {
            ticketRepostiory.deleteByShowId(showId);
        } catch (DataAccessException ex) {
            throw new TicketDeletionException("Failed deleting tickets for showId: " + showId, ex);
        }
    }

    @Transactional
    public TicketDTO bookTicket(TicketDTO ticketDTO) {

//          Check if the show exists or not
        showClient.validateShowExists(ticketDTO.getShowId());

        List<Long> seatIds = ticketDTO.getSeatIds();
//            Check whether the seats are available for the show
        List<Long> showSeatIds = seatClient.getSeats(ticketDTO.getShowId(), ticketDTO.getSeatIds());

        if(seatIds.size() != showSeatIds.size()){
            throw new BookingFailedException("The requested seats are not available, Please try other seats!");
        }

        List<Long> lockedSeats = seatClient.lockSeats(showSeatIds,300);

//      Fetching users name before saving the ticket.
        Long userId = userClient.getUserIdByEmail(getJwt().getClaim("email").toString());

        Ticket ticket = new Ticket();
        ticket.setShowId(ticket.getShowId());
        ticket.setUserId(userId);

        ticket = ticketRepostiory.save(ticket);

        List<Long> bookedSeatIds = seatClient.bookSeats(lockedSeats, ticket.getTicketId());

//            Unlock Seats
        seatClient.unlockSeats(ticket.getTicketId(), bookedSeatIds);

        return modelMapper.map(ticket, TicketDTO.class);
    }

    @Transactional
    public TicketDTO cancelTicket(long ticketId) {

            Ticket ticket = ticketRepostiory.findById(ticketId)
                    .orElseThrow(() -> new TicketNotFoundException("Ticket not found, Check ticket Id!"));

//            get showSeats by ticket
//            Mark the booked seats as free
            List<Long> bookedSeats = seatClient.cancelSeats(ticketId);

            TicketDTO response = new TicketDTO();
            response.setShowId(ticket.getShowId());
            response.setSeatIds(bookedSeats);

            ticketRepostiory.deleteById(ticketId);

            return response;
    }
}








