package com.example.bookmyshow.booking.client;

import java.util.List;

public interface SeatClient {
    List<Long> getSeats(Long showId, List<Long> seatIds);
    List<Long> lockSeats(List<Long> seatIds, int seconds);
    List<Long> bookSeats(List<Long> seatIds, long ticketId);
    void unlockSeats(Long ticketId, List<Long> seatIds);
    List<Long> cancelSeats(Long ticketId);

}





