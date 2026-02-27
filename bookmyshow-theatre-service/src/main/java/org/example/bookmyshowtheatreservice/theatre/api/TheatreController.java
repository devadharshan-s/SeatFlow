package org.example.bookmyshowtheatreservice.theatre.api;

import org.example.bookmyshowtheatreservice.common.dto.ApiResponse;
import org.example.bookmyshowtheatreservice.theatre.api.dto.TheatreDTO;
import org.example.bookmyshowtheatreservice.theatre.api.dto.TheatreResponseDTO;
import org.example.bookmyshowtheatreservice.theatre.service.TheatreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreService theatreService;

    @GetMapping("/getAllTheatres")
    public ResponseEntity<ApiResponse<List<TheatreResponseDTO>>> getAllTheatres() {
        return theatreService.getAllTheatres();
    }

    @GetMapping("/getTheatre/{theatreId}")
    public ResponseEntity<ApiResponse<TheatreDTO>> getTheatre(@PathVariable long theatreId) {
        return theatreService.getTheatre(theatreId);
    }

    @PostMapping("/createTheatre")
    public ResponseEntity<ApiResponse<TheatreResponseDTO>> createTheatre(@RequestBody TheatreDTO theatreDTO) {
        return theatreService.createTheatre(theatreDTO);
    }

    @PatchMapping("/updateTheatre/{TheatreId}")
    public ResponseEntity<ApiResponse<TheatreResponseDTO>> updateTheatre(@PathVariable long TheatreId, @RequestBody TheatreDTO theatreDTO) {
        return theatreService.updateTheatre(TheatreId, theatreDTO);
    }

    @DeleteMapping("/deleteTheatre")
    public ResponseEntity<ApiResponse<TheatreResponseDTO>> deleteTheatre(@RequestParam long theatreId) {
        return theatreService.deleteTheatre(theatreId);
    }
}





