package com.example.bookmyshow.theatre.api;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.theatre.api.dto.ScreenDTO;
import com.example.bookmyshow.theatre.api.dto.ScreenResponseDTO;
import com.example.bookmyshow.theatre.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @GetMapping("/getScreen")
    public ResponseEntity<ApiResponse<ScreenResponseDTO>> getScreen(@RequestParam Long screenId) {
        ScreenResponseDTO screen = screenService.getScreenByScreenId(screenId);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Screen fetched successfully",
                        screen,
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/getScreensByTheatre/{theatreId}")
    public ResponseEntity<ApiResponse<List<ScreenResponseDTO>>> getScreens(@PathVariable long theatreId) {
        List<ScreenResponseDTO> screens = screenService.getScreensByTheatre(theatreId);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Screens fetched successfully",
                        screens,
                        LocalDateTime.now()
                )
        );
    }

    @PostMapping("/createScreen")
    public ResponseEntity<ApiResponse<ScreenResponseDTO>> addScreen(@RequestBody ScreenDTO screenDTO) {
        ScreenResponseDTO screen = screenService.addScreen(screenDTO);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Screen created successfully",
                        screen,
                        LocalDateTime.now()
                )
        );
    }

    @PatchMapping("/updateScreen/{screenId}")
    public ResponseEntity<ApiResponse<ScreenResponseDTO>> updateScreen(@RequestBody ScreenDTO screenDTO, @PathVariable long screenId) {
        ScreenResponseDTO screen = screenService.updateScreen(screenId, screenDTO);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Screen updated successfully",
                        screen,
                        LocalDateTime.now()
                )
        );
    }

    @DeleteMapping("/deleteScreen")
    public ResponseEntity<ApiResponse<Void>> deleteScreen(@RequestParam long screenId) {
        screenService.deleteScreen(screenId);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Screen deleted successfully",
                        null,
                        LocalDateTime.now()
                )
        );
    }
}
