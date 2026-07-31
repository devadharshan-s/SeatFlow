package org.example.bookmyshowshowservice.show.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmyshowshowservice.common.exception.TicketNotFoundException;
import org.example.bookmyshowshowservice.show.exception.SeatOperationException;
import org.example.bookmyshowshowservice.show.exception.ShowSeatNotFoundException;
import org.example.bookmyshowshowservice.show.model.ShowSeat;
import org.example.bookmyshowshowservice.show.repository.ShowSeatRepository;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Redis-backed short-term seat hold used during the "select seats" -> "book"
 * window.
 *
 * <p>
 * Each seat hold is an atomic Lua script operation keyed by show seat id, owned
 * by a ticket id and
 * given a TTL equal to the hold duration. A per-ticket key tracks the set of
 * held seat ids so the
 * whole hold can be released/confirmed in one shot. This is intentionally
 * separate from
 * {@link LockService}, which persists a {@code lockedUntil} column in the DB
 * for longer-lived
 * locks.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeatHoldService {

    private static final String SEAT_HOLD_KEY_PREFIX = "seat:hold:";
    private static final String TICKET_HOLD_KEY_PREFIX = "ticket:hold:";

    private final ShowSeatRepository showSeatRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    /**
     * Hold the given show seats for a ticket for {@code holdSeconds}.
     * Uses atomic Lua script execution to acquire all locks or none.
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
        long ttlMs = holdSeconds * 1000L;
        String ticketKey = ticketHoldKey(ticketId);
        String joinedSeats = joinSeatIds(showSeatIds);

        List<Object> keys = new ArrayList<>();
        for (Long seatId : showSeatIds) {
            keys.add(seatHoldKey(seatId));
        }

        String luaScript = "-- Step 1: Check if any of the requested seats are already held\n" +
                "for i, key in ipairs(KEYS) do\n" +
                "    if redis.call('exists', key) == 1 then\n" +
                "        return 0\n" +
                "    end\n" +
                "end\n" +
                "-- Step 2: Set holds on all seats with the specified TTL (PX/milliseconds)\n" +
                "local ttl = ARGV[2]\n" +
                "local owner = ARGV[1]\n" +
                "for i, key in ipairs(KEYS) do\n" +
                "    redis.call('set', key, owner, 'PX', ttl)\n" +
                "end\n" +
                "-- Step 3: Register the ticket tracker mapping to track these seats\n" +
                "redis.call('set', ARGV[3], ARGV[4], 'PX', ttl)\n" +
                "return 1";

        Long result = redissonClient.getScript(org.redisson.client.codec.StringCodec.INSTANCE).eval(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                keys,
                owner,
                String.valueOf(ttlMs),
                ticketKey,
                joinedSeats);

        if (result == null || result == 0) {
            throw new SeatOperationException("Failed to hold one or more seats: already held or locked");
        }

        log.info("Held {} seats for ticket {} atomically using Redisson Lua script", showSeatIds.size(), ticketId);
        return showSeatIds;
    }

    /**
     * Release every Redis hold tied to a ticket (e.g. on cancel / expiry).
     * Only deletes seat keys still owned by this ticket, so a seat re-held by
     * another
     * session is never accidentally released.
     *
     * @return true if a hold actually existed for the ticket
     */
    public boolean releaseHold(Long ticketId) {
        validateTicketId(ticketId);
        String owner = String.valueOf(ticketId);
        String ticketKey = ticketHoldKey(ticketId);

        String luaScript = "-- Step 1: Retrieve list of seat IDs associated with this ticket\n" +
                "local joined = redis.call('get', KEYS[1])\n" +
                "if not joined then\n" +
                "    return 0\n" +
                "end\n" +
                "local seatKeyPrefix = ARGV[2]\n" +
                "local owner = ARGV[1]\n" +
                "-- Step 2: Loop and release each seat only if currently owned by this ticket\n" +
                "for seatId in string.gmatch(joined, '([^,]+)') do\n" +
                "    local seatKey = seatKeyPrefix .. seatId\n" +
                "    if redis.call('get', seatKey) == owner then\n" +
                "        redis.call('del', seatKey)\n" +
                "    end\n" +
                "end\n" +
                "-- Step 3: Delete ticket hold tracker\n" +
                "redis.call('del', KEYS[1])\n" +
                "return 1";

        Long result = redissonClient.getScript(org.redisson.client.codec.StringCodec.INSTANCE).eval(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                List.<Object>of(ticketKey),
                owner,
                SEAT_HOLD_KEY_PREFIX);

        boolean released = result != null && result == 1;
        log.info("Released holds for ticket {} atomically using Redisson Lua: {}", ticketId, released);
        return released;
    }

    /**
     * Confirm a hold: the booking is about to be persisted, so clear the Redis
     * holds for this
     * ticket and return the held seat ids.
     */
    public List<Long> confirmHold(Long ticketId) {
        validateTicketId(ticketId);
        String owner = String.valueOf(ticketId);
        String ticketKey = ticketHoldKey(ticketId);

        String joined = stringRedisTemplate.opsForValue().get(ticketKey);
        if (joined == null) {
            throw new SeatOperationException("No active hold found for ticket: " + ticketId);
        }

        List<Long> seatIds = parseSeatIds(joined);

        String luaScript = "-- Step 1: Retrieve list of seat IDs associated with this ticket\n" +
                "local joined = redis.call('get', KEYS[1])\n" +
                "if not joined then\n" +
                "    return 0\n" +
                "end\n" +
                "local seatKeyPrefix = ARGV[2]\n" +
                "local owner = ARGV[1]\n" +
                "-- Step 2: Loop and release each seat only if currently owned by this ticket\n" +
                "for seatId in string.gmatch(joined, '([^,]+)') do\n" +
                "    local seatKey = seatKeyPrefix .. seatId\n" +
                "    if redis.call('get', seatKey) == owner then\n" +
                "        redis.call('del', seatKey)\n" +
                "    end\n" +
                "end\n" +
                "-- Step 3: Delete ticket hold tracker\n" +
                "redis.call('del', KEYS[1])\n" +
                "return 1";

        redissonClient.getScript(org.redisson.client.codec.StringCodec.INSTANCE).eval(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                List.<Object>of(ticketKey),
                owner,
                SEAT_HOLD_KEY_PREFIX);

        log.info("Confirmed and cleared holds for ticket {} atomically using Redisson Lua", ticketId);
        return seatIds;
    }

    /**
     * Absolute time at which the hold on a seat expires, or {@code null} if the
     * seat is not held.
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

    private void validateTicketId(Long ticketId) {
        if (ticketId == null || ticketId <= 0) {
            throw new TicketNotFoundException("Invalid ticket id: " + ticketId);
        }
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
