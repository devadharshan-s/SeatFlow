package org.example.bookmyshowtheatreservice.theatre.service;

import org.example.bookmyshowtheatreservice.common.dto.ApiResponse;
import org.example.bookmyshowtheatreservice.theatre.exception.ScreenNotFoundException;
import org.example.bookmyshowtheatreservice.theatre.exception.SeatNotFoundException;
import org.example.bookmyshowtheatreservice.theatre.api.dto.SeatDTO;
import org.example.bookmyshowtheatreservice.theatre.api.dto.SeatRequestDTO;
import org.example.bookmyshowtheatreservice.theatre.api.dto.SeatResponseDTO;
import org.example.bookmyshowtheatreservice.theatre.model.Screen;
import org.example.bookmyshowtheatreservice.theatre.model.Seat;
import org.example.bookmyshowtheatreservice.theatre.repository.ScreenRepository;
import org.example.bookmyshowtheatreservice.theatre.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService {

    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Map<Long, SeatResponseDTO> getSeats(List<Long> seatIds){
        List<Seat> seats = seatRepository.findSeatsBySeatIds(seatIds);

        if(seats == null || seats.isEmpty()){
            throw new SeatNotFoundException("No seats found for the given IDs");
        }

        Map<Long, SeatResponseDTO> response = new HashMap<>();

        seats.forEach(seat -> {
            SeatResponseDTO seatDTO = new SeatResponseDTO();
            seatDTO.setSeatId(seat.getSeatId());
            seatDTO.setSeatNumber(seat.getSeatNumber());
            seatDTO.setRowNumber(seat.getRowNumber());
            seatDTO.setPrice(seat.getPrice());
            seatDTO.setCategory(seat.getCategory());
            response.put(seat.getSeatId(), seatDTO);
        });

        return response;
    }
    @Transactional
    public List<SeatResponseDTO> getSeatsByScreenId(long screenId){
            List<Seat> seats = seatRepository.findSeatByScreen_ScreenId(screenId)
                    .orElseThrow(() -> new ScreenNotFoundException("Screen not found for the given ID: " + screenId));

            List<SeatResponseDTO> seatDTOS = seats.stream()
                    .map(
                            seat -> {
                                SeatResponseDTO seatDTO = new SeatResponseDTO();
                                seatDTO.setSeatId(seat.getSeatId());
                                seatDTO.setSeatNumber(seat.getSeatNumber());
                                seatDTO.setRowNumber(seat.getRowNumber());
                                seatDTO.setPrice(seat.getPrice());
                                seatDTO.setCategory(seat.getCategory());
                                return seatDTO;
                            }
                    ).toList();

            return seatDTOS;
    }

    @Transactional
    public ResponseEntity<ApiResponse<SeatDTO>> getSeat(long seatId){
        try {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new Exception("Seat not found!"));

            SeatDTO seatDTO = new SeatDTO();
            seatDTO.setSeatNumber(seat.getSeatNumber());
            seatDTO.setRowNumber(seat.getRowNumber());
            seatDTO.setPrice(seat.getPrice());
            seatDTO.setCategory(seat.getCategory());

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Seat found Succesfully for the Seat ID: " + seatId,
                            seatDTO,
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public SeatRequestDTO createSeat(SeatRequestDTO seatRequestDTO) {

        try {
//        Check screen if exists
            Screen screen = screenRepository.findByScreenId(seatRequestDTO.getScreenId())
                    .orElseThrow(() -> new Exception("Screen not found"));


            for(SeatDTO seat : seatRequestDTO.getSeats()){
                Seat saveSeat = new Seat();
                saveSeat.setScreen(screen);
                saveSeat.setSeatNumber(seat.getSeatNumber());
                saveSeat.setRowNumber(seat.getRowNumber());
                saveSeat.setPrice(seat.getPrice());
                saveSeat.setCategory(seat.getCategory());
                seatRepository.save(saveSeat);
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
        return seatRequestDTO;
    }

    @Transactional
    public ResponseEntity<ApiResponse<SeatResponseDTO>> updateSeat(long seatId, SeatDTO seatDTO){
        try {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new Exception("Seat not found!"));

            if(seatDTO.getSeatNumber() > 0){
                seat.setSeatNumber(seatDTO.getSeatNumber());
            }
            if(Character.isWhitespace(seatDTO.getRowNumber()) || seatDTO.getRowNumber() != '\u0000') {
                seat.setRowNumber(seatDTO.getRowNumber());
            }
            if (seatDTO.getPrice() > 0.00){
                seat.setPrice(seatDTO.getPrice());
            }
            if(seatDTO.getCategory() != null){
                seat.setCategory(seatDTO.getCategory());
            }

            seatRepository.save(seat);

            SeatResponseDTO seatResponseDTO = new SeatResponseDTO();
            seatResponseDTO.setSeatId(seat.getSeatId());
            seatResponseDTO.setSeatNumber(seat.getSeatNumber());
            seatResponseDTO.setRowNumber(seat.getRowNumber());
            seatResponseDTO.setPrice(seat.getPrice());
            seatResponseDTO.setCategory(seat.getCategory());

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Seat updated Succesfully for the Seat ID: " + seatId,
                            seatResponseDTO,
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> deleteSeat(long seatId){
        try {

            seatRepository.deleteSeatBySeatId(seatId);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Seat deleted Succesfully for the Seat ID: " + seatId,
                            null,
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }
}





