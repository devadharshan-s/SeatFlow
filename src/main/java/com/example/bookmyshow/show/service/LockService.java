package com.example.bookmyshow.show.service;

import com.example.bookmyshow.show.exception.SeatNotFoundException;
import com.example.bookmyshow.show.exception.SeatOperationException;
import com.example.bookmyshow.show.model.ShowSeat;
import com.example.bookmyshow.show.repository.ShowSeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {

    private final ShowSeatRepository showSeatRepository;

    @Transactional
    public List<Long> lockSeats(List<Long> seatIds, int seconds) {

            if(seatIds == null || seatIds.isEmpty())
                throw new SeatNotFoundException("❌ Seats are not found, Please check Seat Ids!");

            List<ShowSeat> showSeats = showSeatRepository.findShowSeatByShowSeatIdIs(seatIds);

            for (ShowSeat seat : showSeats) {
                if (seat.getLockedUntil() != null && seat.getLockedUntil().isAfter(LocalDateTime.now())) {
                    log.error(seat.getShowSeatId() + " is already locked!");
                    throw new SeatOperationException("❌ Seats are already locked, Please try again!");
                }
                seat.setLockedUntil(LocalDateTime.now().plusSeconds(seconds));
            }

            showSeatRepository.saveAll(showSeats);

            return showSeats.stream().map(ShowSeat::getShowSeatId).toList();

    }
}




