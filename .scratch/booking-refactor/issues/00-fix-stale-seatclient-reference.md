# 00 — Fix stale seatClient.bookSeats() reference in TicketService

**What to build:** Line 134 of `TicketService.bookTicket()` calls `seatClient.bookSeats(...)` directly instead of `resilientSeatClient.bookSeats(...)`, bypassing the Resilience4j retry and circuit breaker decorator. This is a one-line bug fix that must land before any further refactoring touches this method.

**Blocked by:** None — can start immediately.

**Status:** ready-for-agent

- [ ] `seatClient.bookSeats(ticket.getTicketId(), heldSeats)` is replaced with `resilientSeatClient.bookSeats(ticket.getTicketId(), heldSeats)` in `TicketService`
- [ ] The `SeatClient` import can be removed from `TicketService` if no other direct references remain
- [ ] Service compiles and starts successfully on port 8085
