package com.example.bookmyshow.show.service;

import com.example.bookmyshow.exception.TicketNotFoundException;
import com.example.bookmyshow.show.api.dto.SeatAvailabilityResponse;
import com.example.bookmyshow.show.client.SeatClient;
import com.example.bookmyshow.show.client.TicketClient;
import com.example.bookmyshow.show.exception.SeatNotFoundException;
import com.example.bookmyshow.show.exception.SeatOperationException;
import com.example.bookmyshow.show.exception.ShowNotFoundException;
import com.example.bookmyshow.show.exception.ShowSeatNotFoundException;
import com.example.bookmyshow.show.model.ShowSeat;
import com.example.bookmyshow.show.model.Shows;
import com.example.bookmyshow.show.repository.ShowSeatRepository;
import com.example.bookmyshow.show.repository.ShowsRepository;
import com.example.bookmyshow.theatre.api.dto.SeatResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowSeatService {

    private final ShowSeatRepository showSeatRepository;
    private final ShowsRepository showsRepository;
    private final SeatClient seatClient;
    private final TicketClient ticketClient;

    @Transactional
    public List<SeatAvailabilityResponse> getShowSeats(Long showId, String status) {
        showsRepository.findByShowId(showId)
                .orElseThrow(() -> new ShowNotFoundException("Show not found"));

        List<ShowSeat> showSeats = showSeatRepository.findShowSeatsByShow_ShowId(showId);
        if (showSeats.isEmpty()) {
            return List.of();
        }

        Map<Long, ShowSeat> showSeatsMap = showSeats.stream()
                .collect(Collectors.toMap(ShowSeat::getSeatId, seat -> seat));

        Map<Long, SeatResponseDTO> seatDetails = seatClient.getSeats(showSeats.stream().map(ShowSeat::getSeatId).toList());

        List<SeatAvailabilityResponse> response = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String requestedStatus = status == null ? "ALL" : status.toUpperCase();

        seatDetails.forEach((seatId, seat) -> {
            ShowSeat showSeat = showSeatsMap.get(seatId);
            if (showSeat == null) {
                return;
            }

            String actualStatus;
            if (Boolean.TRUE.equals(showSeat.getIsBooked())) {
                actualStatus = "BOOKED";
            } else if (showSeat.getLockedUntil() != null && showSeat.getLockedUntil().isAfter(now)) {
                actualStatus = "LOCKED";
            } else {
                actualStatus = "AVAILABLE";
            }

            if (!requestedStatus.equals("ALL") && !actualStatus.equals(requestedStatus)) {
                return;
            }

            SeatAvailabilityResponse seats = new SeatAvailabilityResponse();
            seats.setSeatId(seatId);
            seats.setSeatNumber(seat.getSeatNumber());
            seats.setRowNumber(String.valueOf(seat.getRowNumber()));
            seats.setPrice(seat.getPrice());
            seats.setCategory(seat.getCategory());
            seats.setLockedUntil(showSeat.getLockedUntil());
            seats.setBooked(showSeat.getIsBooked());
            seats.setStatus(actualStatus);

            response.add(seats);
        });

        return response;
    }

    @Transactional
    public List<Long> resolveShowSeatIds(Long showId, List<Long> seatIds) {
        List<Long> responseIds = showSeatRepository.findShowSeats(showId, seatIds)
                .stream().map(ShowSeat::getShowSeatId)
                .toList();

        if (responseIds.isEmpty()) {
            throw new ShowSeatNotFoundException("No matching show seats found for showId: " + showId);
        }

        return responseIds;
    }

    @Transactional
    public List<Long> getShowSeatsByTicketId(Long ticketId) {
        List<ShowSeat> bookedSeats = showSeatRepository.findShowSeatsByTicketId(ticketId);

        if (bookedSeats.isEmpty()) {
            throw new TicketNotFoundException("Ticket not found, Check ticket Id!");
        }

        return bookedSeats.stream().map(ShowSeat::getShowSeatId).toList();
    }

    public List<Long> bookSeats(List<Long> showSeatIds, Long ticketId) {
        validateTicketId(ticketId);

        List<ShowSeat> showSeats = showSeatRepository.findShowSeatByShowSeatIdIs(showSeatIds);
        if (showSeats.isEmpty()) {
            throw new SeatOperationException("No show seats found to book");
        }

        showSeats.forEach(seat -> {
            seat.setIsBooked(Boolean.TRUE);
            seat.setTicketId(ticketId);
        });

        showSeatRepository.saveAll(showSeats);

        log.info("Seats booked successfully");

        return showSeats.stream().map(ShowSeat::getShowSeatId).toList();
    }

    @Transactional
    public Boolean unlockSeats(Long ticketId, List<Long> seatIds) {
        validateTicketId(ticketId);

        List<ShowSeat> showSeats = showSeatRepository.findShowSeatByShowSeatIdIs(seatIds);
        if (showSeats.isEmpty()) {
            throw new SeatOperationException("No show seats found to unlock");
        }

        for (ShowSeat showSeat : showSeats) {
            if (!ticketId.equals(showSeat.getTicketId())) {
                throw new SeatOperationException("Seat " + showSeat.getShowSeatId() + " is not associated with ticketId: " + ticketId);
            }
            showSeat.setLockedUntil(null);
        }

        showSeatRepository.saveAll(showSeats);
        return Boolean.TRUE;
    }

    @Transactional
    public List<Long> cancelSeats(Long ticketId) {
        List<ShowSeat> bookedSeats = showSeatRepository.findShowSeatsByTicketId(ticketId);

        if (bookedSeats.isEmpty()) {
            throw new SeatNotFoundException("No seats found for ticketId: " + ticketId);
        }

        bookedSeats.forEach(seat -> {
            seat.setIsBooked(Boolean.FALSE);
            seat.setTicketId(null);
        });

        showSeatRepository.saveAll(bookedSeats);

        log.info("Seats cancelled successfully");

        return bookedSeats.stream().map(ShowSeat::getShowSeatId).toList();
    }

    private void validateTicketId(Long ticketId) {
        if (ticketId == null || ticketId <= 0) {
            throw new TicketNotFoundException("Ticket not found, Check ticket Id!");
        }
        ticketClient.validateTicketExists(ticketId);
    }
}




