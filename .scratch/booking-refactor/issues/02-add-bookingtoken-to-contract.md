# 02 — Add bookingToken to TicketDTO and SeatClient contract

**What to build:** Introduce the `bookingToken` (UUID) field into the booking flow's data contract. The `TicketDTO` gains a `bookingToken` field. The `SeatClient` and `ResilientSeatClient` methods (`holdSeats`, `bookSeats`, `releaseHold`, `confirmHold`) are updated to accept `bookingToken` (String) instead of `ticketId` (Long) as their correlation parameter. This is the **expand** phase — both the old `ticketId`-based signatures and new `bookingToken`-based signatures can coexist temporarily until show-service endpoints are updated in ticket 03.

**Blocked by:** 00 — Fix stale seatClient reference (so we modify a clean baseline).

**Status:** ready-for-agent

- [ ] `TicketDTO` has a `bookingToken` (String/UUID) field
- [ ] `SeatClient` Feign interface methods accept `bookingToken` (or both `bookingToken` and `ticketId` during transition)
- [ ] `ResilientSeatClient` delegate methods updated to match
- [ ] Booking-service compiles
