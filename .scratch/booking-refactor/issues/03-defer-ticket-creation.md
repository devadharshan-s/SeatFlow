# 03 — Refactor bookTicket to defer ticket creation until confirmation

**What to build:** Rewrite `TicketService.bookTicket()` so that it no longer calls `ticketRepository.save()` before seat hold/booking. Instead, the method uses the `bookingToken` from `TicketDTO` to hold and book seats via `ResilientSeatClient`. It returns a response containing the `bookingToken` and held seat IDs — but no database `Ticket` row is persisted. Introduce a new `confirmBooking(bookingToken, showId, userId, seatIds)` method that creates the `Ticket` entity in the database, to be called only after payment succeeds. The `getJwt()` helper should be refactored to return `Optional<Jwt>` as part of this change.

**Blocked by:** 02 — bookingToken must exist in the data contract.

**Status:** ready-for-agent

- [ ] `bookTicket()` does NOT call `ticketRepository.save()` — no database row is created during booking
- [ ] `bookTicket()` uses `bookingToken` as the correlation key for `holdSeats()` and `bookSeats()` calls
- [ ] A new `confirmBooking(...)` method exists that persists the `Ticket` entity
- [ ] `getJwt()` is refactored to return `Optional<Jwt>`
- [ ] No orphaned ticket rows can exist from failed bookings
- [ ] Service compiles and starts successfully
