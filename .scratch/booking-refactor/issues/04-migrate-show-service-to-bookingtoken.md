# 04 — Migrate show-service callers from ticketId to bookingToken validation

**What to build:** In show-service, `ShowSeatService.validateTicketId()` currently makes a cross-service Feign call to booking-service's `/validateTicket/{ticketId}` endpoint. Since seat operations now use `bookingToken` (a UUID string), this cross-service call is unnecessary — replace it with a local null/format validation of the `bookingToken` parameter. `SeatHoldService.validateTicketId()` already does only local validation, so it just needs its parameter renamed. The `TicketClient` Feign interface in show-service can have its `validateTicketExists` method removed once no caller references it.

**Blocked by:** 03 — show-service endpoints must accept `bookingToken` before we can remove `ticketId` validation.

**Status:** ready-for-agent

- [ ] `ShowSeatService.validateTicketId()` no longer makes a Feign call to booking-service
- [ ] `ShowSeatService` seat operations accept and validate `bookingToken` instead of `ticketId`
- [ ] `SeatHoldService` seat operations accept `bookingToken` where applicable
- [ ] `TicketClient.validateTicketExists()` is no longer called from show-service
- [ ] Show-service compiles and starts on port 8086
- [ ] Booking flow (hold → book → confirm) works end-to-end with `bookingToken`
