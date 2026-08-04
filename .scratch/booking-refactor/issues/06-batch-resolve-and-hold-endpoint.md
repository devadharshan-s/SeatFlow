# 06 — Add batch resolve-and-hold endpoint in show-service

**What to build:** Introduce a composite endpoint in show-service that combines seat resolution and seat holding into a single atomic operation: `POST /shows/{showId}/hold-seats` accepting `{ bookingToken, seatIds, holdSeconds }`. This eliminates one HTTP round-trip from the booking flow (currently: resolve → hold are two separate Feign calls). The booking-service gains a corresponding `holdAndResolveSeats(...)` method in `SeatClient` and `ResilientSeatClient`, and `bookTicket()` is updated to call the batched endpoint instead of two sequential calls.

**Blocked by:** 03 — bookTicket must use bookingToken before we can batch the operations around it.

**Status:** ready-for-agent

- [ ] Show-service exposes `POST /shows/{showId}/hold-seats` that resolves seat IDs and holds them atomically
- [ ] The endpoint returns the resolved `showSeatIds` on success
- [ ] Booking-service `SeatClient` has a `holdAndResolveSeats(...)` Feign method
- [ ] `ResilientSeatClient` wraps it with `@Retry` and `@CircuitBreaker`
- [ ] `bookTicket()` uses the batched call instead of separate resolve + hold
- [ ] Latency for the booking flow is reduced by one HTTP round-trip
- [ ] Both services compile and start successfully
