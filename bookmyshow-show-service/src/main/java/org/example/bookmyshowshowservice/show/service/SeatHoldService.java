package org.example.bookmyshowshowservice.show.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmyshowshowservice.common.exception.TicketNotFoundException;
import org.example.bookmyshowshowservice.show.exception.SeatOperationException;
import org.example.bookmyshowshowservice.show.exception.ShowSeatNotFoundException;
import org.example.bookmyshowshowservice.show.model.ShowSeat;
import org.example.bookmyshowshowservice.show.repository.ShowSeatRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Redis-backed short-term seat hold used during the "select seats" -> "book" window.
 *
 * <p>Each seat hold is an atomic {@code SETNX} keyed by show seat id, owned by a ticket id and
 * given a TTL equal to the hold duration. A per-ticket key tracks the set of held seat ids so the
 * whole hold can be released/confirmed in one shot. This is intentionally separate from
 * {@link LockService}, which persists a {@code lockedUntil} column in the DB for longer-lived
 * locks — see CLAUDE.md for the overlap caveat.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeatHoldService {

    private static final String SEAT_HOLD_KEY_PREFIX = "seat:hold:";
    private static final String TICKET_HOLD_KEY_PREFIX = "ticket:hold:";

    private final ShowSeatRepository showSeatRepository;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Hold the given show seats for a ticket for {@code holdSeconds}.
     * Uses Redis SETNX per seat so two tickets cannot hold the same seat.
     *
     * @return the list of show seat ids that were successfully held
     */
    public List<Long> holdSeats(Long ticketId, List<Long> showSeatIds, int holdSeconds) {
        validateTicketId(ticketId);
        if (showSeatIds == null || showSeatIds.isEmpty()) {
            throw new ShowSeatNotFoundException("Show seat ids must not be empty");
        }
        if (holdSeconds <= 0) {
            throw new SeatOperationException("Hold duration must be greater than zero seconds");
        }

        List<ShowSeat> showSeats = showSeatRepository.findShowSeatByShowSeatIdIs(showSeatIds);
        if (showSeats.size() != showSeatIds.size()) {
            throw new ShowSeatNotFoundException("One or more show seat ids are invalid");
        }

        for (ShowSeat showSeat : showSeats) {
            if (Boolean.TRUE.equals(showSeat.getIsBooked())) {
                throw new SeatOperationException("Seat already booked: " + showSeat.getShowSeatId());
            }
        }

        String owner = String.valueOf(ticketId);
        Duration ttl = Duration.ofSeconds(holdSeconds);
        List<Long> acquiredSeatIds = new ArrayList<>();

        try {
            for (Long seatId : showSeatIds) {
                String seatKey = seatHoldKey(seatId);
                Boolean acquired = stringRedisTemplate.opsForValue()
                        .setIfAbsent(seatKey, owner, ttl);
                if (!Boolean.TRUE.equals(acquired)) {
                    throw new SeatOperationException("Seat already held by another session: " + seatId);
                }
                acquiredSeatIds.add(seatId);
            }

            stringRedisTemplate.opsForValue()
                    .set(ticketHoldKey(ticketId), joinSeatIds(acquiredSeatIds), ttl);

            log.info("Held {} seats for ticket {}", acquiredSeatIds.size(), ticketId);
            return acquiredSeatIds;
        } catch (RuntimeException ex) {
            rollbackAcquiredSeats(ticketId, acquiredSeatIds);
            throw ex;
        }
    }

    /**
     * Release every Redis hold tied to a ticket (e.g. on cancel / expiry).
     * Only deletes seat keys still owned by this ticket, so a seat re-held by another
     * session is never accidentally released.
     *
     * @return true if a hold actually existed for the ticket
     */
    public boolean releaseHold(Long ticketId) {
        validateTicketId(ticketId);
        String owner = String.valueOf(ticketId);

        List<Long> seatIds = parseSeatIds(stringRedisTemplate.opsForValue().get(ticketHoldKey(ticketId)));
        for (Long seatId : seatIds) {
            String seatKey = seatHoldKey(seatId);
            if (owner.equals(stringRedisTemplate.opsForValue().get(seatKey))) {
                stringRedisTemplate.delete(seatKey);
            }
        }
        Boolean deleted = stringRedisTemplate.delete(ticketHoldKey(ticketId));
        log.info("Released holds for ticket {}", ticketId);
        return Boolean.TRUE.equals(deleted);
    }

    /**
     * Confirm a hold: the booking is about to be persisted, so clear the Redis holds for this
     * ticket and return the held seat ids.
     */
    public List<Long> confirmHold(Long ticketId) {
        validateTicketId(ticketId);
        String joined = stringRedisTemplate.opsForValue().get(ticketHoldKey(ticketId));
        if (joined == null) {
            throw new SeatOperationException("No active hold found for ticket: " + ticketId);
        }
        String owner = String.valueOf(ticketId);
        List<Long> seatIds = parseSeatIds(joined);
        for (Long seatId : seatIds) {
            String seatKey = seatHoldKey(seatId);
            if (owner.equals(stringRedisTemplate.opsForValue().get(seatKey))) {
                stringRedisTemplate.delete(seatKey);
            }
        }
        stringRedisTemplate.delete(ticketHoldKey(ticketId));
        log.info("Confirmed holds for ticket {}", ticketId);
        return seatIds;
    }

    /**
     * Absolute time at which the hold on a seat expires, or {@code null} if the seat is not held.
     */
    public LocalDateTime getHoldExpiry(Long showSeatId) {
        if (showSeatId == null || showSeatId <= 0) {
            throw new ShowSeatNotFoundException("Invalid show seat id: " + showSeatId);
        }
        Long secondsRemaining = stringRedisTemplate.getExpire(seatHoldKey(showSeatId));
        if (secondsRemaining == null || secondsRemaining < 0) {
            return null;
        }
        return LocalDateTime.now().plusSeconds(secondsRemaining);
    }

    // --- helpers ---

    private void validateTicketId(Long ticketId) {
        if (ticketId == null || ticketId <= 0) {
            throw new TicketNotFoundException("Invalid ticket id: " + ticketId);
        }
    }

    private void rollbackAcquiredSeats(Long ticketId, List<Long> acquiredSeatIds) {
        String owner = String.valueOf(ticketId);
        for (Long seatId : acquiredSeatIds) {
            String seatKey = seatHoldKey(seatId);
            if (owner.equals(stringRedisTemplate.opsForValue().get(seatKey))) {
                stringRedisTemplate.delete(seatKey);
            }
        }
        log.warn("Rolled back {} acquired seat holds for ticket {}", acquiredSeatIds.size(), ticketId);
    }

    private String seatHoldKey(Long showSeatId) {
        return SEAT_HOLD_KEY_PREFIX + showSeatId;
    }

    private String ticketHoldKey(Long ticketId) {
        return TICKET_HOLD_KEY_PREFIX + ticketId;
    }

    private String joinSeatIds(List<Long> seatIds) {
        StringBuilder sb = new StringBuilder();
        for (Long id : seatIds) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(id);
        }
        return sb.toString();
    }

    private List<Long> parseSeatIds(String joined) {
        List<Long> result = new ArrayList<>();
        if (joined == null || joined.isBlank()) {
            return result;
        }
        for (String part : joined.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(Long.parseLong(trimmed));
            }
        }
        return result;
    }
}
