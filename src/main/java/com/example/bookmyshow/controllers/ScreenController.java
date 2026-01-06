package com.example.bookmyshow.controllers;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.TheatreDTO.ScreenDTO;
import com.example.bookmyshow.DTO.TheatreDTO.ScreenResponseDTO;
import com.example.bookmyshow.services.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @GetMapping("/getScreen")
    public ResponseEntity<ApiResponse<ScreenResponseDTO>> getScreen(@RequestParam Long screenId) {
        return screenService.getScreenByScreenId(screenId);
    }

    @GetMapping("/getScreensByTheatre/{theatreId}")
    public ResponseEntity<ApiResponse<List<ScreenResponseDTO>>> getScreens(@PathVariable long theatreId) {
        return screenService.getScreensByTheatre(theatreId);
    }

    @PostMapping("/createScreen")
    public ResponseEntity<ApiResponse<ScreenDTO>> addScreen(@RequestBody ScreenDTO screenDTO) {
        return screenService.addScreen(screenDTO);
    }

    @PatchMapping("/updateScreen/{screenId}")
    public ResponseEntity<ApiResponse<ScreenResponseDTO>> updateScreen(@RequestBody ScreenDTO screenDTO, @PathVariable long screenId) {
        return screenService.updateScreen(screenId, screenDTO);
    }

    @DeleteMapping("/deleteScreen")
    public ResponseEntity<ApiResponse<?>> deleteScreen(@RequestParam long screenId) {
        return screenService.deleteScreen(screenId);
    }
}
