package org.example.bookmyshowbookingservice.booking.service;

import org.example.bookmyshowbookingservice.booking.api.dto.SeatAvailabilityResponse;
import org.example.bookmyshowbookingservice.booking.api.dto.TicketDTO;
import org.example.bookmyshowbookingservice.booking.client.SeatClient;
import org.example.bookmyshowbookingservice.booking.client.ShowClient;
import org.example.bookmyshowbookingservice.booking.client.ShowSeatClient;
import org.example.bookmyshowbookingservice.booking.client.UserClient;
import org.example.bookmyshowbookingservice.booking.exception.BookingFailedException;
import org.example.bookmyshowbookingservice.booking.exception.ConcurrentTicketUpdateException;
import org.example.bookmyshowbookingservice.booking.exception.InvalidShowIdException;
import org.example.bookmyshowbookingservice.booking.exception.TicketCancellationException;
import org.example.bookmyshowbookingservice.booking.exception.TicketDeletionException;
import org.example.bookmyshowbookingservice.booking.model.Ticket;
import org.example.bookmyshowbookingservice.booking.repository.TicketRepostiory;
import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.example.bookmyshowbookingservice.common.exception.TicketNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
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
    public List<SeatAvailabilityResponse> getSeats(Long showId, String status) {
        ApiResponse<List<SeatAvailabilityResponse>> response = showSeatClient.seats(showId, status);
        if (response == null || response.getData() == null) {
            throw new BookingFailedException("Seat lookup returned empty response for showId: " + showId);
        }
        return response.getData();
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

        ApiResponse<Object> showResponse = showClient.getShowById(ticketDTO.getShowId());
        if (showResponse == null || showResponse.getData() == null) {
            throw new BookingFailedException("Show validation failed for showId: " + ticketDTO.getShowId());
        }

        List<Long> seatIds = ticketDTO.getSeatIds();

        ApiResponse<List<Long>> resolvedSeatResponse = seatClient.resolveShowSeatIds(ticketDTO.getShowId(), seatIds);
        if (resolvedSeatResponse == null || resolvedSeatResponse.getData() == null) {
            throw new BookingFailedException("Seat resolution failed for showId: " + ticketDTO.getShowId());
        }
        List<Long> showSeatIds = resolvedSeatResponse.getData();

        if (seatIds.size() != showSeatIds.size()) {
            throw new BookingFailedException("The requested seats are not available, Please try other seats!");
        }

        ApiResponse<List<Long>> lockedSeatResponse = seatClient.lockSeats(300, showSeatIds);
        if (lockedSeatResponse == null || lockedSeatResponse.getData() == null) {
            throw new BookingFailedException("Can't lock seats, check lock service!");
        }
        List<Long> lockedSeats = lockedSeatResponse.getData();

        ApiResponse<Long> userResponse = userClient.getUserByEmail(getJwt().getClaim("email").toString());
        if (userResponse == null || userResponse.getData() == null) {
            throw new BookingFailedException("User lookup failed");
        }
        Long userId = userResponse.getData();

        Ticket ticket = new Ticket();
        ticket.setShowId(ticketDTO.getShowId());
        ticket.setUserId(userId);

        ticket = ticketRepostiory.save(ticket);

        ApiResponse<List<Long>> bookedSeatResponse = seatClient.bookSeats(ticket.getTicketId(), lockedSeats);
        if (bookedSeatResponse == null || bookedSeatResponse.getData() == null) {
            throw new BookingFailedException("Seat booking failed for seatIds: " + lockedSeats);
        }
        List<Long> bookedSeatIds = bookedSeatResponse.getData();

        ApiResponse<Boolean> unlockResponse = seatClient.unlockSeats(ticket.getTicketId(), bookedSeatIds);
        if (unlockResponse == null || unlockResponse.getData() == null || !unlockResponse.getData()) {
            throw new BookingFailedException("Seat unlock failed for ticketId: " + ticket.getTicketId());
        }

        return modelMapper.map(ticket, TicketDTO.class);
    }

    @Transactional
    public TicketDTO cancelTicket(long ticketId) {

        Ticket ticket = ticketRepostiory.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found, Check ticket Id!"));

        ApiResponse<List<Long>> cancelResponse = seatClient.cancelSeats(ticketId);
        if (cancelResponse == null || cancelResponse.getData() == null) {
            throw new TicketCancellationException(String.valueOf(ticketId));
        }

        List<Long> bookedSeats = cancelResponse.getData();

        TicketDTO response = new TicketDTO();
        response.setShowId(ticket.getShowId());
        response.setSeatIds(bookedSeats);

        try {
            ticketRepostiory.delete(ticket);
            ticketRepostiory.flush();
        } catch (OptimisticLockingFailureException ex) {
            throw new ConcurrentTicketUpdateException(
                    "Ticket was modified concurrently. Please retry cancellation.",
                    ex
            );
        }

        return response;
    }
}
