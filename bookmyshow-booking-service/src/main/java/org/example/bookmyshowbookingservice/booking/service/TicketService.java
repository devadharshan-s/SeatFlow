package org.example.bookmyshowbookingservice.booking.service;

import org.example.bookmyshowbookingservice.booking.api.dto.TicketDTO;
import org.example.bookmyshowbookingservice.booking.client.ResilientSeatClient;
import org.example.bookmyshowbookingservice.booking.client.ShowClient;
import org.example.bookmyshowbookingservice.booking.client.UserClient;
import org.example.bookmyshowbookingservice.booking.constants.ReservationStatus;
import org.example.bookmyshowbookingservice.booking.exception.BookingFailedException;
import org.example.bookmyshowbookingservice.booking.exception.ConcurrentTicketUpdateException;
import org.example.bookmyshowbookingservice.booking.exception.InvalidShowIdException;
import org.example.bookmyshowbookingservice.booking.exception.TicketCancellationException;
import org.example.bookmyshowbookingservice.booking.exception.TicketDeletionException;
import org.example.bookmyshowbookingservice.booking.model.Reservation;
import org.example.bookmyshowbookingservice.booking.model.Ticket;
import org.example.bookmyshowbookingservice.booking.repository.ReservationRepository;
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
    private final ReservationRepository reservationRepository;
    private final ShowClient showClient;
    private final ResilientSeatClient resilientSeatClient;
    private final UserClient userClient;
    private final ModelMapper modelMapper;

    public static java.util.Optional<Jwt> getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return java.util.Optional.of(jwt);
        }
        return java.util.Optional.empty();
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

    public TicketDTO reserveSeats(TicketDTO ticketDTO) {

        String bookingToken = ticketDTO.getBookingToken();

        // Checking for booking token(UUID)
        if (bookingToken == null || bookingToken.isBlank()) {
            throw new BookingFailedException("bookingToken is required and must be from frontend!");
        }

        ApiResponse<Object> showResponse = showClient.getShowById(ticketDTO.getShowId());

        if (showResponse == null || showResponse.getData() == null) {
            throw new BookingFailedException("Show validation failed for showId: " + ticketDTO.getShowId());
        }

        List<Long> seatIds = ticketDTO.getSeatIds();

        ApiResponse<List<Long>> resolvedSeatResponse = resilientSeatClient.resolveShowSeatIds(ticketDTO.getShowId(),
                seatIds);

        if (resolvedSeatResponse == null || resolvedSeatResponse.getData() == null) {
            throw new BookingFailedException("Seat resolution failed for showId: " + ticketDTO.getShowId());
        }

        List<Long> showSeatIds = resolvedSeatResponse.getData();

        if (seatIds.size() != showSeatIds.size()) {
            throw new BookingFailedException("The requested seats are not available, Please try other seats!");
        }

        Long userId = 1L;
        try {
            java.util.Optional<Jwt> jwtOpt = getJwt();
            if (jwtOpt.isPresent()) {
                Object emailClaim = jwtOpt.get().getClaim("email");
                if (emailClaim != null) {
                    ApiResponse<Long> userResponse = userClient.getUserByEmail(emailClaim.toString());
                    if (userResponse != null && userResponse.getData() != null) {
                        userId = userResponse.getData();
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Security context did not contain a valid JWT user, using default userId=1: {}", ex.getMessage());
        }

        ApiResponse<List<Long>> heldSeatResponse = resilientSeatClient.holdSeats(bookingToken, 300, showSeatIds);

        if (heldSeatResponse == null || heldSeatResponse.getData() == null || heldSeatResponse.getData().isEmpty()) {
            throw new BookingFailedException("Can't hold seats, check hold service!");
        }

        List<Long> heldSeats = heldSeatResponse.getData();

        Reservation reservation = new Reservation();

        reservation.setBookingToken(bookingToken);
        reservation.setShowId(ticketDTO.getShowId());
        reservation.setUserId(userId);
        reservation.setShowSeatIds(heldSeats);
        reservation.setAmount(ticketDTO.getAmountPaid());
        reservation.setStatus(ReservationStatus.PENDING);

        reservationRepository.save(reservation);

        TicketDTO result = new TicketDTO();
        result.setBookingToken(bookingToken);
        result.setShowId(ticketDTO.getShowId());
        result.setSeatIds(seatIds);
        result.setShowSeatIds(heldSeats);
        result.setUserId(userId);
        result.setAmountPaid(ticketDTO.getAmountPaid());
        return result;

    }

    @Transactional
    public TicketDTO confirmBooking(String bookingToken) {

        Reservation reservation = reservationRepository.findByBookingToken(bookingToken)
                .orElseThrow(
                        () -> new BookingFailedException("No reservation found for booking token: " + bookingToken));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BookingFailedException(
                    "Booking status is not PENDING, it is " + reservation.getStatus() + " " + bookingToken);
        }

        ApiResponse<List<Long>> bookedSeats = resilientSeatClient.bookSeats(bookingToken, reservation.getShowSeatIds());

        if (bookedSeats == null || bookedSeats.getData() == null) {
            throw new BookingFailedException("Booking failed for token: " + bookingToken);
        }

        Ticket ticket = new Ticket();
        ticket.setShowId(reservation.getShowId());
        ticket.setUserId(reservation.getUserId());
        ticket.setShowSeatIds(bookedSeats.getData());
        ticket.setAmountPaid(reservation.getAmount());
        ticket = ticketRepostiory.save(ticket);

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        TicketDTO response = modelMapper.map(ticket, TicketDTO.class);
        response.setBookingToken(bookingToken);
        response.setAmountPaid(reservation.getAmount());

        return response;
        // List<Long> bookedSeatIds = bookedSeats.getData();

        // Ticket ticket = new Ticket();
        // ticket.setShowId(ticketDTO.getShowId());
        // ticket.setUserId(ticketDTO.getUserId());
        // ticket.setShowSeatIds(bookedSeatIds);
        // ticket = ticketRepostiory.save(ticket);

        // TicketDTO response = modelMapper.map(ticket, TicketDTO.class);
        // response.setBookingToken(ticketDTO.getBookingToken());
        // response.setAmountPaid(ticketDTO.getAmountPaid());

        // return response;
    }

    @Transactional
    public TicketDTO cancelTicket(long ticketId) {

        Ticket ticket = ticketRepostiory.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found, Check ticket Id!"));

        ApiResponse<List<Long>> cancelResponse = resilientSeatClient.cancelSeats(ticketId);

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
                    ex);
        }

        return response;
    }
}
