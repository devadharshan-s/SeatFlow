# 01 — Move getSeats endpoint from booking-service to show-service

**What to build:** The `/selectSeats/{showId}` endpoint currently lives in booking-service's `TicketController` and is a pure pass-through to show-service via `ShowSeatClient`. Move seat availability querying to show-service where it belongs (likely `ShowSeatController`), then remove the pass-through from booking-service: delete `getSeats()` from `TicketService`, remove the endpoint from `TicketController`, and remove the `ShowSeatClient` Feign interface + `ShowSeatClientFallback`. Update the frontend to call show-service directly for seat availability.

**Blocked by:** None — can start immediately (independent of ticket 00).

**Status:** ready-for-agent

- [ ] Show-service exposes a `/selectSeats/{showId}` (or equivalent) endpoint that returns `List<SeatAvailabilityResponse>`
- [ ] Booking-service no longer contains `getSeats()` in `TicketService`
- [ ] Booking-service no longer contains the `/selectSeats/{showId}` endpoint in `TicketController`
- [ ] `ShowSeatClient` and `ShowSeatClientFallback` are removed from booking-service (or verified that no other method depends on them)
- [ ] Frontend seat availability calls route to show-service
- [ ] Both services compile and start successfully
