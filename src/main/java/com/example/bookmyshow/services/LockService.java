package com.example.bookmyshow.services;

import com.example.bookmyshow.models.Theatre.ShowSeat;
import com.example.bookmyshow.repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {

    private final ShowSeatRepository showSeatRepository;

    @Transactional
    public boolean lockSeats(List<ShowSeat> seats, int seconds){
//        lets give a 5 min window for the seat to be locked.
        LocalDateTime now = LocalDateTime.now();

        try {
//            Validate seats are not locked.
            for (ShowSeat seat : seats) {
                if (Boolean.TRUE.equals(seat.getIsBooked())){
                    log.error("Seat is already booked! " + seat.getSeat().getSeatId());
                    return false;
                }
                if(seat.getLockedUntil() != null && !seat.getLockedUntil().isAfter(LocalDateTime.now())){
                    log.error("Seat is already locked by someone! " + seat.getSeat().getSeatId());
                    return false;
                }
            }

            LocalDateTime lockedUntil = now.plusSeconds(seconds);

//            If the seats are not locked.
//            Lock em
            for(ShowSeat seat : seats){
                seat.setLockedUntil(lockedUntil);
            }

            showSeatRepository.saveAll(seats);

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
