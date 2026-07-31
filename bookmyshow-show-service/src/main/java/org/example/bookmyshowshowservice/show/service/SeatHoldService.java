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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Redis-backed short-term seat hold and selection locking.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeatHoldService {

    private static final String SEAT_HOLD_KEY_PREFIX = "seat:hold:";
    private static final String TICKET_HOLD_KEY_PREFIX = "ticket:hold:";
    private static final String SEAT_LOCK_KEY_PREFIX = "seat:lock:";

    private final ShowSeatRepository showSeatRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    /**
     * Lock the given show seats transiently in Redis using an idempotency token.
     */
    public List<Long> lockSeats(List<Long> showSeatIds, int seconds, String bookingToken) {
        if (showSeatIds == null || showSeatIds.isEmpty()) {
            throw new ShowSeatNotFoundException("Show seat ids must not be empty");
        }
        if (seconds <= 0) {
            throw new SeatOperationException("Lock duration must be greater than zero seconds");
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

        String token = (bookingToken != null && !bookingToken.isBlank()) ? bookingToken : "ANONYMOUS";
        long ttlMs = seconds * 1000L;
        List<Object> keys = new ArrayList<>();
        for (Long seatId : showSeatIds) {
            keys.add(seatLockKey(seatId));
        }
        for (Long seatId : showSeatIds) {
            keys.add(seatHoldKey(seatId));
        }

        String luaScript =
                "-- KEYS[1..N] are lock keys, KEYS[N+1..2N] are hold keys\n" +
                "for i, key in ipairs(KEYS) do\n" +
                "    if redis.call('exists', key) == 1 then\n" +
                "        return 0\n" +
                "    end\n" +
                "end\n" +
                "local ttl = ARGV[2]\n" +
                "local token = ARGV[1]\n" +
                "local half = #KEYS / 2\n" +
                "for i = 1, half do\n" +
                "    redis.call('set', KEYS[i], token, 'PX', ttl)\n" +
                "end\n" +
                "return 1";

        Long result = redissonClient.getScript(org.redisson.client.codec.StringCodec.INSTANCE).eval(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                keys,
                token,
                String.valueOf(ttlMs)
        );

        if (result == null || result == 0) {
            throw new SeatOperationException("Failed to lock one or more seats: already locked or held");
        }

        log.info("Locked {} seats transiently in Redis with token: {}", showSeatIds.size(), token);
        return showSeatIds;
    }

    /**
     * Hold the given show seats for a ticket, upgrading any active selection locks owned by the token.
     */
    public List<Long> holdSeats(Long ticketId, List<Long> showSeatIds, int holdSeconds, String bookingToken) {
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
        String token = (bookingToken != null && !bookingToken.isBlank()) ? bookingToken : "ANONYMOUS";

        List<Object> keys = new ArrayList<>();
        for (Long seatId : showSeatIds) {
            keys.add(seatHoldKey(seatId));
        }

        String luaScript =
                "-- Step 1: Check if any of the requested seats are already held by another ticket\n" +
                "-- and verify that any selection locks belong to our bookingToken\n" +
                "for i, key in ipairs(KEYS) do\n" +
                "    if redis.call('exists', key) == 1 then\n" +
                "        return 0\n" +
                "    end\n" +
                "    local lockKey = string.gsub(key, 'hold', 'lock')\n" +
                "    local currentToken = redis.call('get', lockKey)\n" +
                "    if currentToken and currentToken ~= ARGV[5] then\n" +
                "        return 0 -- Lock owned by a different session\n" +
                "    end\n" +
                "end\n" +
                "-- Step 2: Set holds on all seats with specified TTL and clear selection locks\n" +
                "local ttl = ARGV[2]\n" +
                "local owner = ARGV[1]\n" +
                "for i, key in ipairs(KEYS) do\n" +
                "    redis.call('set', key, owner, 'PX', ttl)\n" +
                "    local lockKey = string.gsub(key, 'hold', 'lock')\n" +
                "    redis.call('del', lockKey)\n" +
                "end\n" +
                "-- Step 3: Register the ticket tracker mapping\n" +
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
                joinedSeats,
                token
        );

        if (result == null || result == 0) {
            throw new SeatOperationException("Failed to hold one or more seats: already held or locked by others");
        }

        log.info("Held {} seats for ticket {} atomically, upgrading from token: {}", showSeatIds.size(), ticketId, token);
        return showSeatIds;
    }

    /**
     * Get active selection locks and their remaining TTLs for a list of show seat IDs.
     */
    public Map<Long, LocalDateTime> getActiveLocks(List<Long> showSeatIds) {
        if (showSeatIds == null || showSeatIds.isEmpty()) {
            return Map.of();
        }

        List<Object> keys = showSeatIds.stream()
                .map(this::seatLockKey)
                .collect(Collectors.toList());

        String luaScript =
                "local ttls = {}\n" +
                "for i, key in ipairs(KEYS) do\n" +
                "    ttls[i] = redis.call('pttl', key)\n" +
                "end\n" +
                "return ttls";

        List<Long> ttls = redissonClient.getScript(org.redisson.client.codec.StringCodec.INSTANCE).eval(
                RScript.Mode.READ_ONLY,
                luaScript,
                RScript.ReturnType.MULTI,
                keys
        );

        Map<Long, LocalDateTime> lockExpiries = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < showSeatIds.size(); i++) {
            Long ttlMs = ttls.get(i);
            if (ttlMs != null && ttlMs > 0) {
                lockExpiries.put(showSeatIds.get(i), now.plus(ttlMs, java.time.temporal.ChronoUnit.MILLIS));
            }
        }
        return lockExpiries;
    }

    public boolean releaseHold(Long ticketId) {
        validateTicketId(ticketId);
        String owner = String.valueOf(ticketId);
        String ticketKey = ticketHoldKey(ticketId);

        String luaScript =
                "local joined = redis.call('get', KEYS[1])\n" +
                "if not joined then\n" +
                "    return 0\n" +
                "end\n" +
                "local seatKeyPrefix = ARGV[2]\n" +
                "local owner = ARGV[1]\n" +
                "for seatId in string.gmatch(joined, '([^,]+)') do\n" +
                "    local seatKey = seatKeyPrefix .. seatId\n" +
                "    if redis.call('get', seatKey) == owner then\n" +
                "        redis.call('del', seatKey)\n" +
                "    end\n" +
                "end\n" +
                "redis.call('del', KEYS[1])\n" +
                "return 1";

        Long result = redissonClient.getScript(org.redisson.client.codec.StringCodec.INSTANCE).eval(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                List.<Object>of(ticketKey),
                owner,
                SEAT_HOLD_KEY_PREFIX
        );

        boolean released = result != null && result == 1;
        log.info("Released holds for ticket {} atomically using Redisson Lua: {}", ticketId, released);
        return released;
    }

    public List<Long> confirmHold(Long ticketId) {
        validateTicketId(ticketId);
        String owner = String.valueOf(ticketId);
        String ticketKey = ticketHoldKey(ticketId);

        String joined = stringRedisTemplate.opsForValue().get(ticketKey);
        if (joined == null) {
            throw new SeatOperationException("No active hold found for ticket: " + ticketId);
        }

        List<Long> seatIds = parseSeatIds(joined);

        String luaScript =
                "local joined = redis.call('get', KEYS[1])\n" +
                "if not joined then\n" +
                "    return 0\n" +
                "end\n" +
                "local seatKeyPrefix = ARGV[2]\n" +
                "local owner = ARGV[1]\n" +
                "for seatId in string.gmatch(joined, '([^,]+)') do\n" +
                "    local seatKey = seatKeyPrefix .. seatId\n" +
                "    if redis.call('get', seatKey) == owner then\n" +
                "        redis.call('del', seatKey)\n" +
                "    end\n" +
                "end\n" +
                "redis.call('del', KEYS[1])\n" +
                "return 1";

        redissonClient.getScript(org.redisson.client.codec.StringCodec.INSTANCE).eval(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                List.<Object>of(ticketKey),
                owner,
                SEAT_HOLD_KEY_PREFIX
        );

        log.info("Confirmed and cleared holds for ticket {} atomically using Redisson Lua", ticketId);
        return seatIds;
    }

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

    private String seatLockKey(Long showSeatId) {
        return SEAT_LOCK_KEY_PREFIX + showSeatId;
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
