package com.example.bookmyshow.theatre.service;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.theatre.api.dto.TheatreDTO;
import com.example.bookmyshow.theatre.api.dto.TheatreResponseDTO;
import com.example.bookmyshow.theatre.model.Theatre;
import com.example.bookmyshow.theatre.repository.TheatreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheatreService {

    private final ModelMapper modelMapper;
    private final TheatreRepository theatreRepository;

    @Transactional
    public ResponseEntity<ApiResponse<TheatreDTO>> getTheatre(long theatreId) {
        try {
            Theatre theatre = theatreRepository.findByTheatreId(theatreId)
                    .orElseThrow(() -> new Exception("Theatre not found!"));

            TheatreDTO theatreDTO = modelMapper.map(theatre, TheatreDTO.class);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Theatre found Successfully for the Theatre ID: " + theatreId,
                            theatreDTO,
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Theatre Not found: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<List<TheatreResponseDTO>>> getAllTheatres(){
        try {
            List<Theatre> theatres = (List<Theatre>) theatreRepository.findAll();
            List<TheatreResponseDTO> response = theatres.stream()
                    .map(theatre -> modelMapper.map(theatre, TheatreResponseDTO.class))
                    .toList();
            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ All Theatres fetched successfully!",
                            response,
                            LocalDateTime.now()
                    ));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Theatre Not found: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<TheatreResponseDTO>> createTheatre(TheatreDTO theatreDTO) {
        try {
            Theatre theatre = modelMapper.map(theatreDTO, Theatre.class);
            theatre = theatreRepository.save(theatre);
            TheatreResponseDTO theatreResponseDTO = modelMapper.map(theatre, TheatreResponseDTO.class);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Theatre created successfully!",
                            theatreResponseDTO,
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Theatre was not created: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<TheatreResponseDTO>> updateTheatre(long theatreId, TheatreDTO theatreDTO){
        try {
            Theatre theatre = theatreRepository.findByTheatreId(theatreId)
                    .orElseThrow(() -> new Exception("Theatre not found!"));

            if(theatreDTO.getName() != null) theatre.setTheatreName(theatreDTO.getName());
            if(theatreDTO.getContact() != null) theatre.setContact(theatreDTO.getContact());
            if(theatreDTO.getEmail() != null) theatre.setEmail(theatreDTO.getEmail());

            theatre = theatreRepository.save(theatre);

            TheatreResponseDTO theatreResponseDTO = modelMapper.map(theatre, TheatreResponseDTO.class);
            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Theatre updated successfully!",
                            theatreResponseDTO,
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Theatre was not updated: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<TheatreResponseDTO>> deleteTheatre(long theatreId) {
        try {
            Theatre theatre = theatreRepository.findByTheatreId(theatreId)
                    .orElseThrow(() -> new Exception("Theatre not found!"));

            TheatreResponseDTO theatreResponseDTO = modelMapper.map(theatre, TheatreResponseDTO.class);

            theatreRepository.deleteByTheatreId(theatreId);
            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Theatre deleted successfully!",
                            theatreResponseDTO,
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Theatre was not deleted: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

}




