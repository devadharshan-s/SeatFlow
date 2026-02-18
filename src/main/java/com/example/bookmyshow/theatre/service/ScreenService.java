package com.example.bookmyshow.theatre.service;

import com.example.bookmyshow.exception.ScreenNotFoundException;
import com.example.bookmyshow.exception.TheatreNotFoundException;
import com.example.bookmyshow.theatre.api.dto.ScreenDTO;
import com.example.bookmyshow.theatre.api.dto.ScreenResponseDTO;
import com.example.bookmyshow.theatre.model.Screen;
import com.example.bookmyshow.theatre.model.Theatre;
import com.example.bookmyshow.theatre.repository.ScreenRepository;
import com.example.bookmyshow.theatre.repository.SeatRepository;
import com.example.bookmyshow.theatre.repository.TheatreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
    public List<ScreenResponseDTO> getScreensByTheatre(long theatreId) {

        Theatre theatre = theatreRepository.findByTheatreId(theatreId)
                .orElseThrow(() -> new TheatreNotFoundException("Theatre not found for given Theatre Id"));

        List<Screen> screens = screenRepository.findScreensByTheatre(theatre)
                .orElseThrow(() -> new ScreenNotFoundException("Screen not found for given Screen Id"));

        boolean hasMismatchedTheatre = screens.stream()
                .anyMatch(screen -> screen.getTheatre() == null || screen.getTheatre().getTheatreId() != theatre.getTheatreId());

        if (hasMismatchedTheatre) {
            throw new ScreenNotFoundException("One or more screens do not belong to the given theatre");
        }

        return screens.stream()
                .map(screen -> modelMapper.map(screen, ScreenResponseDTO.class))
                .toList();
    }

    @Transactional
    public ScreenResponseDTO getScreenByScreenId(long screenId) {

        Screen screen = screenRepository.findByScreenId(screenId)
                .orElseThrow(() -> new ScreenNotFoundException("Screen not found for given Screen Id"));

        return modelMapper.map(screen, ScreenResponseDTO.class);
    }

    @Transactional
    public ScreenResponseDTO updateScreen(long screenId, ScreenDTO screenDTO) {

        Screen screen = screenRepository.findByScreenId(screenId)
                .orElseThrow(() -> new ScreenNotFoundException("Screen not found for given Screen Id"));

        if (screenDTO.getScreenName() != null && !screen.getScreenName().equals(screenDTO.getScreenName())) {
            screen.setScreenName(screenDTO.getScreenName());
        }
        if (screenDTO.getCapacity() != 0 && screenDTO.getCapacity() != screen.getCapacity()) {
            screen.setCapacity(screenDTO.getCapacity());
        }
        if (screenDTO.getNumberOfRows() != 0 && screenDTO.getNumberOfRows() != screen.getNumberOfRows()) {
            screen.setNumberOfRows(screenDTO.getNumberOfRows());
        }

        screen = screenRepository.save(screen);

        return modelMapper.map(screen, ScreenResponseDTO.class);
    }

    @Transactional
    public ScreenResponseDTO addScreen(ScreenDTO screenDTO) {

        long theatreId = screenDTO.getTheatreId();
        String screenName = screenDTO.getScreenName();
        int capacity = screenDTO.getCapacity();
        int numberOfRows = screenDTO.getNumberOfRows();

        if (theatreId <= 0) {
            throw new IllegalArgumentException("Theatre Id must be greater than 0");
        }
        if (screenName == null || screenName.isBlank()) {
            throw new IllegalArgumentException("Screen name is required");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        if (numberOfRows <= 0) {
            throw new IllegalArgumentException("Number of rows must be greater than 0");
        }
        if (numberOfRows > capacity) {
            throw new IllegalArgumentException("Number of rows cannot exceed capacity");
        }

        Theatre theatre = theatreRepository.findByTheatreId(theatreId)
                .orElseThrow(() -> new TheatreNotFoundException("Theatre not found for given Theatre Id: " + theatreId));

        Screen screen = new Screen();
        screen.setTheatre(theatre);
        screen.setScreenName(screenName.trim());
        screen.setCapacity(capacity);
        screen.setNumberOfRows(numberOfRows);
        screen = screenRepository.save(screen);

        return modelMapper.map(screen, ScreenResponseDTO.class);
    }

    @Transactional
    public void deleteScreen(long screenId) {
        if (screenId <= 0) {
            throw new IllegalArgumentException("Screen Id must be greater than 0");
        }

        Screen screen = screenRepository.findByScreenId(screenId)
                .orElseThrow(() -> new ScreenNotFoundException("Screen not found for given Screen Id"));

        seatRepository.deleteSeatByScreen_ScreenId(screen.getScreenId());
        screenRepository.delete(screen);
    }
}
