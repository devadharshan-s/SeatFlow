package org.example.bookmyshowshowservice.show.service;

import org.example.bookmyshowshowservice.common.dto.ApiResponse;
import org.example.bookmyshowshowservice.common.exception.TicketNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.bookmyshowshowservice.show.api.dto.SeatAvailabilityResponse;
import org.example.bookmyshowshowservice.show.client.SeatClient;
import org.example.bookmyshowshowservice.show.client.TicketClient;
import org.example.bookmyshowshowservice.show.exception.SeatNotFoundException;
import org.example.bookmyshowshowservice.show.exception.SeatOperationException;
import org.example.bookmyshowshowservice.show.exception.ShowNotFoundException;
import org.example.bookmyshowshowservice.show.exception.ShowSeatNotFoundException;
import org.example.bookmyshowshowservice.show.model.ShowSeat;
import org.example.bookmyshowshowservice.show.repository.ShowSeatRepository;
import org.example.bookmyshowshowservice.show.repository.ShowsRepository;
import org.example.bookmyshowshowservice.show.client.dto.SeatResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
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

    @Autowired
    @Lazy
    private ShowSeatService self;

        private final ShowSeatRepository showSeatRepository;
    private final ShowsRepository showsRepository;
    private final SeatClient seatClient;
    private final TicketClient ticketClient;
    private final SeatHoldService seatHoldService;
    private final org.springframework.cache.CacheManager cacheManager;
    private final LockService lockService;

    @Cacheable(value = "staticSeatDetails", key = "#showId")
    public Map<Long, SeatResponseDTO> getStaticSeatDetails(Long showId, List<Long> seatIds) {

        ApiResponse<Map<Long, SeatResponseDTO>> seatsResponse = seatClient.getSeats(seatIds);
        return (seatsResponse != null && seatsResponse.getData() != null) ? seatsResponse.getData() : Map.of();

    }

    @CacheEvict(value = "staticSeatDetails", key = "#showId")
    public void evictStaticSeatDetails(Long showId) {

        log.info("Evicted static seat details cache for showId: {}", showId);

    }

    public Long getShowIdForSeat(Long showSeatId) {

        ShowSeat seat = showSeatRepository.findById(showSeatId)
                .orElseThrow(() -> new ShowSeatNotFoundException("Seat not found"));
        return seat.getShow().getShowId();

    }

    @Cacheable(value = "showSeatsCache", key = "#showId + '-' + (#status != null ? #status.toUpperCase() : 'ALL')")
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

        List<Long> seatIds = showSeats.stream().map(ShowSeat::getSeatId).toList();
        Map<Long, SeatResponseDTO> seatDetails = self.getStaticSeatDetails(showId, seatIds);

        List<SeatAvailabilityResponse> response = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String requestedStatus = status == null ? "ALL" : status.toUpperCase();

        seatDetails.forEach((seatId, seat) -> {
            ShowSeat showSeat = showSeatsMap.get(seatId);
            if (showSeat == null) {
                return;
            }

            LocalDateTime holdExpiry = seatHoldService.getHoldExpiry(showSeat.getShowSeatId());
            String actualStatus;
            if (Boolean.TRUE.equals(showSeat.getIsBooked())) {
                actualStatus = "BOOKED";
            } else if (holdExpiry != null) {
                actualStatus = "LOCKED";
            } else if (showSeat.getLockedUntil() != null && showSeat.getLockedUntil().isAfter(now)) {
                actualStatus = "LOCKED";
                holdExpiry = showSeat.getLockedUntil();
            } else {
                actualStatus = "AVAILABLE";
            }

            if (!requestedStatus.equals("ALL") && !actualStatus.equals(requestedStatus)) {
                return;
            }

            SeatAvailabilityResponse responseSeat = new SeatAvailabilityResponse();
            responseSeat.setSeatId(seatId);
            responseSeat.setSeatNumber(seat.getSeatNumber());
            responseSeat.setRowNumber(String.valueOf(seat.getRowNumber()));
            responseSeat.setPrice(seat.getPrice());
            responseSeat.setCategory(seat.getCategory());
            responseSeat.setLockedUntil(holdExpiry);
            responseSeat.setBooked(showSeat.getIsBooked());
            responseSeat.setStatus(actualStatus);

            response.add(responseSeat);
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

        Long showId = showSeats.get(0).getShow().getShowId();

        showSeats.forEach(seat -> {
            seat.setIsBooked(Boolean.TRUE);
            seat.setTicketId(ticketId);
        });

        showSeatRepository.saveAll(showSeats);

        log.info("Seats booked successfully");
        evictShowSeatsCache(showId);

        return showSeats.stream().map(ShowSeat::getShowSeatId).toList();
    }

    @Transactional
    public Boolean unlockSeats(Long ticketId, List<Long> seatIds) {
        validateTicketId(ticketId);

        List<ShowSeat> showSeats = showSeatRepository.findShowSeatByShowSeatIdIs(seatIds);
        if (showSeats.isEmpty()) {
            throw new SeatOperationException("No show seats found to unlock");
        }

        Long showId = showSeats.get(0).getShow().getShowId();

        for (ShowSeat showSeat : showSeats) {
            if (!ticketId.equals(showSeat.getTicketId())) {
                throw new SeatOperationException("Seat " + showSeat.getShowSeatId() + " is not associated with ticketId: " + ticketId);
            }
            showSeat.setLockedUntil(null);
        }

        showSeatRepository.saveAll(showSeats);
        evictShowSeatsCache(showId);
        return Boolean.TRUE;
    }

    @Transactional
    public List<Long> cancelSeats(Long ticketId) {
        List<ShowSeat> bookedSeats = showSeatRepository.findShowSeatsByTicketId(ticketId);

        if (bookedSeats.isEmpty()) {
            throw new SeatNotFoundException("No seats found for ticketId: " + ticketId);
        }

        Long showId = bookedSeats.get(0).getShow().getShowId();

        bookedSeats.forEach(seat -> {
            seat.setIsBooked(Boolean.FALSE);
            seat.setTicketId(null);
        });

        showSeatRepository.saveAll(bookedSeats);

        log.info("Seats cancelled successfully");
        evictShowSeatsCache(showId);

        return bookedSeats.stream().map(ShowSeat::getShowSeatId).toList();
    }

    public void evictShowSeatsCache(Long showId) {
        if (showId == null) return;
        org.springframework.cache.Cache cache = cacheManager.getCache("showSeatsCache");
        if (cache != null) {
            cache.evict(showId + "-ALL");
            cache.evict(showId + "-AVAILABLE");
            cache.evict(showId + "-BOOKED");
            cache.evict(showId + "-LOCKED");
            log.info("Evicted showSeatsCache for showId: {}", showId);
        }
    }

    @CircuitBreaker(name = "redisHold", fallbackMethod = "holdSeatsFallback")
    public List<Long> holdSeats(Long ticketId, List<Long> showSeatIds, int holdSeconds) {
        return seatHoldService.holdSeats(ticketId, showSeatIds, holdSeconds);
    }

    public List<Long> holdSeatsFallback(Long ticketId, List<Long> showSeatIds, int holdSeconds, Throwable t) {
        log.error("Redis holdSeats failed, circuit breaker active. Falling back to MySQL LockService. Error: {}", t.getMessage());
        return lockService.lockSeats(showSeatIds, holdSeconds);
    }

    @CircuitBreaker(name = "redisRelease", fallbackMethod = "releaseHoldFallback")
    public Boolean releaseHold(Long ticketId) {
        return seatHoldService.releaseHold(ticketId);
    }

    public Boolean releaseHoldFallback(Long ticketId, Throwable t) {
        log.error("Redis releaseHold failed for ticketId: {}, ignoring since MySQL locks expire automatically. Error: {}", ticketId, t.getMessage());
        return true;
    }

    @CircuitBreaker(name = "redisConfirm", fallbackMethod = "confirmHoldFallback")
    public List<Long> confirmHold(Long ticketId) {
        return seatHoldService.confirmHold(ticketId);
    }

    public List<Long> confirmHoldFallback(Long ticketId, Throwable t) {
        log.error("Redis confirmHold failed for ticketId: {}, falling back to database query. Error: {}", ticketId, t.getMessage());
        try {
            List<Long> seatIds = getShowSeatsByTicketId(ticketId);
            unlockSeats(ticketId, seatIds);
            return seatIds;
        } catch (Exception e) {
            return List.of();
        }
    }

    private void validateTicketId(Long ticketId) {
        if (ticketId == null || ticketId <= 0) {
            throw new TicketNotFoundException("Ticket not found, Check ticket Id!");
        }
        ticketClient.validateTicketExists(ticketId);
    }
}





