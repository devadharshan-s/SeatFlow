package com.example.bookmyshow.services;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.TheatreDTO.ScreenDTO;
import com.example.bookmyshow.DTO.TheatreDTO.ScreenResponseDTO;
import com.example.bookmyshow.models.Theatre.Screen;
import com.example.bookmyshow.models.Theatre.Theatre;
import com.example.bookmyshow.repository.ScreenRepository;
import com.example.bookmyshow.repository.SeatRepository;
import com.example.bookmyshow.repository.TheatreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final TheatreRepository theatreRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ResponseEntity<ApiResponse<List<ScreenResponseDTO>>> getScreensByTheatre(long theatreId) {
        try{
            Theatre theatre = theatreRepository.findByTheatreId(theatreId)
                    .orElseThrow(() -> new Exception("Theatre not found for given Theatre Id"));
            List<Screen> screens = screenRepository.findScreensByTheatre(theatre)
                    .orElseThrow(() -> new Exception("Screen not found for given Screen Id"));

            List<ScreenResponseDTO> response = new ArrayList<>();

            for(Screen screen : screens){
                ScreenResponseDTO screenDTO = modelMapper.map(screen, ScreenResponseDTO.class);
                response.add(screenDTO);
            }

            return ResponseEntity.status(200)
                    .body(new ApiResponse<List<ScreenResponseDTO>>(
                            200,
                            "✅ Screens present in theatre: " + theatre.getTheatreName(),
                            response,
                            LocalDateTime.now()
                    ));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<List<ScreenResponseDTO>>(
                            400,
                            "❌ Screens not found: " +  e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<ScreenResponseDTO>> getScreenByScreenId(long screenId) {
        try {
            Screen screen = screenRepository.findByScreenId(screenId)
                    .orElseThrow(() -> new Exception("Screen not found for given Screen Id"));
            ScreenResponseDTO response = modelMapper.map(screen, ScreenResponseDTO.class);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Screen fetched succesfully! see details below",
                            response,
                            LocalDateTime.now()
                    ));
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Screen not found: " +   e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<ScreenResponseDTO>> updateScreen(long screenId, ScreenDTO screenDTO) {
        try {
            Screen screen = screenRepository.findByScreenId(screenId)
                    .orElseThrow(() -> new Exception("Screen not found for given Screen Id"));

            if(screenDTO.getScreenName() != null) screen.setScreenName(screenDTO.getScreenName());
            if(screenDTO.getCapacity() != 0) screen.setCapacity(screenDTO.getCapacity());
            if(screenDTO.getNumberOfRows() != 0) screen.setNumberOfRows(screenDTO.getNumberOfRows());

            screen = screenRepository.save(screen);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Screen updated succesfully! see details below",
                            modelMapper.map(screen, ScreenResponseDTO.class),
                            LocalDateTime.now()
                    ));
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<ScreenResponseDTO>(
                            400,
                            "❌ Screen not updated: " +   e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }
    @Transactional
    public ResponseEntity<ApiResponse<ScreenDTO>> addScreen(ScreenDTO screenDTO) {
        Theatre theatre = theatreRepository.findById(screenDTO.getTheatreId()).orElse(null);
        Screen screen = new Screen();
        screen.setTheatre(theatre);
        screen.setScreenName(screenDTO.getScreenName());
        screen.setCapacity(screenDTO.getCapacity());
        screen.setNumberOfRows(screenDTO.getNumberOfRows());
        screenRepository.save(screen);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        200,
                        "✅ Screen added Successfully, ScreenID: " + screen.getScreenId(),
                        modelMapper.map(screen,ScreenDTO.class),
                        LocalDateTime.now()
                ));
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> deleteScreen(long screenId) {
        try {
            Screen screen = screenRepository.findById(screenId)
                    .orElseThrow(() -> new Exception("Screen not found! "));

            String screenName = screen.getScreenName();
            String theatreName = screen.getTheatre().getTheatreName();

            seatRepository.deleteSeatByScreen_ScreenId(screen.getScreenId());
            screenRepository.delete(screen);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Screen deleted Successfully! from Theatre " +  theatreName,
                            screenName,
                            LocalDateTime.now()
                    ));
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Screen was not deleted: " +  e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }
}
