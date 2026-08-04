# 05 — Remove validateTicketExists endpoint and clean up dead code

**What to build:** Contract phase — now that show-service no longer calls `validateTicketExists`, and the booking flow no longer creates premature tickets, remove the dead code: delete the `validateTicketExists()` method from `TicketService`, the `/validateTicket/{ticketId}` endpoint from `TicketController`, and the `ShowSeatClient`/`ShowSeatClientFallback` if not already removed in ticket 01. Audit payment-service's `TicketClient.validateTicketExists()` — it should now validate against confirmed tickets only (which always exist post-payment), so the endpoint stays but the method becomes meaningful.

**Blocked by:** 04 — all show-service callers must be migrated first.

**Status:** ready-for-agent

- [ ] `validateTicketExists()` is removed from `TicketService` (or retained only for payment-service's legitimate use)
- [ ] `/validateTicket/{ticketId}` endpoint removed from `TicketController` (or kept for payment-service if still needed)
- [ ] No dead imports or unused fallback classes remain in booking-service
- [ ] Payment-service still compiles and functions (if `validateTicketExists` is retained for its use)
- [ ] All services start cleanly
