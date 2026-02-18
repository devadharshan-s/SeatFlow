package com.example.bookmyshow.theatre.api;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.theatre.api.dto.SeatDTO;
import com.example.bookmyshow.theatre.api.dto.SeatRequestDTO;
import com.example.bookmyshow.theatre.api.dto.SeatResponseDTO;
import com.example.bookmyshow.theatre.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping("/getSeats")
    public ResponseEntity<ApiResponse<Map<Long, SeatResponseDTO>>> getSeats(@RequestBody List<Long> seatIds){

        Map<Long, SeatResponseDTO> response = seatService.getSeats(seatIds);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "✅ Seats fetched Successfully",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/getSeatsByScreen")
    public ResponseEntity<ApiResponse<List<SeatResponseDTO>>> getSeatsByScreen(@RequestParam long screenId){
        List<SeatResponseDTO> responseDTOS = seatService.getSeatsByScreenId(screenId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "✅ Seats fetched Successfully for ScreenId: " + screenId,
                        responseDTOS,
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/getSeatsByScreen/{screenId}")
    public ResponseEntity<ApiResponse<List<SeatResponseDTO>>> getSeatsByScreenPath(@PathVariable long screenId){
        return getSeatsByScreen(screenId);
    }

    @GetMapping("/getSeat/{seatId}")
    public ResponseEntity<ApiResponse<SeatDTO>> getSeatByScreenId(@PathVariable long seatId){
        return seatService.getSeat(seatId);
    }

    @PostMapping("/createSeats")
    public ResponseEntity<ApiResponse<SeatRequestDTO>> createSeat(@RequestBody SeatRequestDTO seatRequestDTO) {
        SeatRequestDTO response =  seatService.createSeat(seatRequestDTO);
        if(response == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "❌ Seats not created!",
                            null,
                            LocalDateTime.now()
                            )
                    );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<SeatRequestDTO>(
                        HttpStatus.OK.value(),
                        "✅ Seats created successfully",
                        response,
                        LocalDateTime.now()
                ));
    }

    @PatchMapping("/updateSeat/{seatId}")
    public ResponseEntity<ApiResponse<SeatResponseDTO>> updateSeat(@RequestBody SeatDTO seatDTO, @PathVariable long seatId){
        return seatService.updateSeat(seatId, seatDTO);
    }

    @DeleteMapping("/deleteSeat/{seatId}")
    public ResponseEntity<ApiResponse<?>> deleteSeat(@PathVariable long seatId){
        return seatService.deleteSeat(seatId);
    }
}
