# 03 — Poll seat status in the frontend and prevent selecting locked/booked seats

**What to build:** `ShowBookingPage` fetches seats once on mount and never refreshes them. If another
user locks seats in the same show, the current window still shows those seats as `AVAILABLE` and lets
the user click them. When the user then tries to lock, the backend rejects the request (seat already
held), but the UI gives a generic "Failed to lock seats" error with no indication of which seats
changed.

Fix the frontend so it:
1. Polls seat availability every 15 seconds so stale states are surfaced without a page reload.
2. **Debounces the poll** — skips the scheduled refresh if a lock or fetch is already in-flight,
   to avoid race-condition flicker (a stale poll response arriving after a successful lock could
   reset the UI back to an intermediate state).
3. After any failed lock attempt, immediately refreshes the seat list.
4. Clears the selected-seat state for any seat that is no longer `AVAILABLE` after a refresh.

**Blocked by:** 01, 02 (so that the backend actually returns correct `LOCKED` statuses before the
frontend tries to render them).

**Status:** ready-for-agent

- [ ] Add an `isPollingActive` ref (not state, to avoid re-render) in `ShowBookingPage.tsx`.
      In the polling `useEffect`, only call `fetchSeats()` if `isPollingActive.current === true` AND
      neither `loading` nor `locking` is true. Set `isPollingActive.current = false` before any
      explicit lock/fetch call and restore it after the call completes.
- [ ] Add a `useEffect` with a `setInterval` (15 000 ms) guarded by `lockedSeatIds.length === 0`.
      Clear the interval on unmount or when `lockedSeatIds.length > 0`.
- [ ] After a failed `lockSeats` call, call `fetchSeats()` before setting the error so the user
      sees the correct seat states alongside the error message.
- [ ] After every `fetchSeats()` resolves, reconcile selection:
      `setSelectedSeats(prev => prev.filter(id => newSeats.some(s => s.seatId === id && s.status === 'AVAILABLE')))`.
- [ ] Verify: open the show in two browser windows; lock a seat in window A; within 15 s, window B
      shows that seat as `LOCKED` (orange) and it is no longer selectable. Rapidly lock in window A
      during an in-flight poll in window B — confirm no flicker/reset occurs.
