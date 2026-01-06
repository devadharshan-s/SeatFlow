package com.example.bookmyshow.services;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.TheatreDTO.SeatDTO.SeatAvailabilityDTO;
import com.example.bookmyshow.models.Theatre.ShowSeat;
import com.example.bookmyshow.models.Theatre.Shows;
import com.example.bookmyshow.repository.ShowSeatRepository;
import com.example.bookmyshow.repository.ShowsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowSeatService {

    private final ShowSeatRepository showSeatRepository;
    private final ShowsRepository showsRepository;

    public ResponseEntity<ApiResponse<List<SeatAvailabilityDTO>>> getShowSeats(long showId, String status){
        try {
            Shows show = showsRepository.findByShowId(showId)
                    .orElseThrow(() -> new Exception("Show not found"));
            List<ShowSeat> availableSeats = showSeatRepository.findByShow(show);



            List<SeatAvailabilityDTO> availableSeatsResponse = availableSeats.stream().map(
                    seat ->{
                        SeatAvailabilityDTO dto = new SeatAvailabilityDTO();

                        dto.setSeatId(seat.getSeat().getSeatId());
                        dto.setRowNumber(String.valueOf(seat.getSeat().getRowNumber()));
                        dto.setSeatNumber(seat.getSeat().getSeatNumber());
                        dto.setPrice(seat.getSeat().getPrice());
                        dto.setCategory(seat.getSeat().getCategory());
                        dto.setLockedUntil(seat.getLockedUntil());

                        if (Boolean.TRUE.equals(seat.getIsBooked())) {
                            dto.setStatus("BOOKED");
                        } else if (seat.getLockedUntil() != null &&
                                seat.getLockedUntil().isAfter(LocalDateTime.now())) {
                            dto.setStatus("LOCKED");
                        } else {
                            dto.setStatus("AVAILABLE");
                        }
                        return dto;
                    }).filter(dto -> status.equalsIgnoreCase("ALL") || dto.getStatus().equalsIgnoreCase(status))
                    .toList();
            return ResponseEntity.status(200)
                    .body(new ApiResponse<List<SeatAvailabilityDTO>>(
                            200,
                            "✅ Seats fetched Successfully for show:  " + showId,
                            availableSeatsResponse,
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<List<SeatAvailabilityDTO>>(
                            400,
                            "❌ Seats not booked, Please try again! for show: " + showId,
                            null,
                            LocalDateTime.now()
                    ));
        }
    }
}
