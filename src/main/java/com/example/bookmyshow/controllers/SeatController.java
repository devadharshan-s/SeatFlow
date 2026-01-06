package com.example.bookmyshow.controllers;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.TheatreDTO.SeatDTO.SeatDTO;
import com.example.bookmyshow.DTO.TheatreDTO.SeatDTO.SeatRequestDTO;
import com.example.bookmyshow.DTO.TheatreDTO.SeatDTO.SeatResponseDTO;
import com.example.bookmyshow.services.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/getSeats")
    public ResponseEntity<ApiResponse<List<SeatResponseDTO>>> getSeatsByScreen(@RequestParam long screenId){
        return seatService.getSeatsByScreenId(screenId);
    }

    @GetMapping("/getSeat/{seatId}")
    public ResponseEntity<ApiResponse<SeatDTO>> getSeatsByScreenId(@PathVariable long seatId){
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
