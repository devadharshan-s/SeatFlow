package com.example.bookmyshow.show.service;

import com.example.bookmyshow.show.api.dto.ShowDTO;
import com.example.bookmyshow.show.api.dto.ShowResponseDTO;
import com.example.bookmyshow.show.client.impl.*;
import com.example.bookmyshow.show.exception.ShowNotFoundException;
import com.example.bookmyshow.show.exception.ShowOperationException;
import com.example.bookmyshow.show.model.ShowSeat;
import com.example.bookmyshow.show.model.Shows;
import com.example.bookmyshow.show.repository.ShowsRepository;
import com.example.bookmyshow.theatre.api.dto.ScreenResponseDTO;
import com.example.bookmyshow.theatre.api.dto.SeatResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowService {

    private final ShowsRepository showRepository;
    private final MovieRestClient movieRestClient;
    private final TheatreRestClient theatreRestClient;
    private final ScreenRestClient screenRestClient;
    private final SeatRestClient seatRestClient;
    private final TicketRestClient ticketRestClient;
    private final ModelMapper modelMapper;

    @Transactional
    public List<ShowResponseDTO> getAllShows(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        Page<Shows> shows = showRepository.findAll(pageable);

        return shows.stream()
                .map(show -> modelMapper.map(show, ShowResponseDTO.class))
                .toList();
    }


    @Transactional
    public ShowDTO getShowById(long showId){

            Shows show = showRepository.findByShowId(showId)
                    .orElseThrow(() -> new ShowNotFoundException("show not found for the id " + showId));
            return modelMapper.map(show, ShowDTO.class);
    }

//    Should only allow to create shows if movie,seat,screen & theatre exists
    @Transactional
    public ShowDTO createShows(ShowDTO show) {
        long screenId = show.getScreenId();
        long theatreId = show.getTheatreId();
        long movieId = show.getMovieId();

        if (screenId <= 0) {
            throw new ShowOperationException("Invalid screen ID");
        }
        if (theatreId <= 0) {
            throw new ShowOperationException("Invalid theatre ID");
        }
        if (movieId <= 0) {
            throw new ShowOperationException("Invalid movie ID");
        }

//      Calls made to check if movie and theatre exist
        movieRestClient.getMovie(movieId);
        theatreRestClient.getTheatre(theatreId);
        ScreenResponseDTO screen = screenRestClient.getScreen(screenId);

//      Validate screen belongs to the theatre
        if (screen.getTheatreId() != theatreId) {
            throw new ShowOperationException("Screen does not belong to the selected theatre");
        }

        if (show.getStartTime() == null || show.getEndTime() == null) {
            throw new ShowOperationException("Both startTime and endTime are required");
        }

        if (!show.getStartTime().isBefore(show.getEndTime())) {
            throw new ShowOperationException("startTime must be before endTime");
        }

        Optional<Shows> conflict = showRepository.findByScreenIdAndStartTimeLessThanAndEndTimeGreaterThan(
                screenId,
                show.getEndTime(),
                show.getStartTime()
        );

        if (conflict.isPresent()) {
            throw new ShowOperationException("Show timing conflicts with existing show: " + conflict.get().getShowId());
        }

        Shows newShow = new Shows();
        newShow.setScreenId(screenId);
        newShow.setMovieId(movieId);
        newShow.setTheatreId(theatreId);
        newShow.setStartTime(show.getStartTime());
        newShow.setEndTime(show.getEndTime());
        newShow = showRepository.save(newShow);

        List<SeatResponseDTO> seats = seatRestClient.getSeatsByScreen(screenId);
        if (seats == null || seats.isEmpty()) {
            throw new ShowOperationException("No seats found for the selected screen");
        }

        for (SeatResponseDTO seat : seats) {
            ShowSeat showSeat = new ShowSeat();
            showSeat.setShow(newShow);
            showSeat.setSeatId(seat.getSeatId());
            showSeat.setIsBooked(false);
            newShow.getShowSeats().add(showSeat);
        }

        newShow = showRepository.save(newShow);

        return modelMapper.map(newShow, ShowDTO.class);
    }

    @Transactional
    public ShowResponseDTO updateShow(long showId, ShowDTO showDTO) {

        if (showId <= 0) {
            throw new ShowNotFoundException("Check entered Show Id: " + showId);
        }

        Shows show = showRepository.findByShowId(showId)
                .orElseThrow(() -> new ShowNotFoundException("Show not found for the given Id!"));

        long effectiveScreenId = show.getScreenId();
        LocalDateTime effectiveStartTime = show.getStartTime();
        LocalDateTime effectiveEndTime = show.getEndTime();

        boolean screenChanged = false;
        boolean timingChanged = false;

//      Check if Screen Id exists
        if (showDTO.getScreenId() > 0) {
            ScreenResponseDTO screen = screenRestClient.getScreen(showDTO.getScreenId());
//      Check if screen belongs to the theatre
            if (screen.getTheatreId() != show.getTheatreId()) {
                throw new ShowOperationException("Screen does not belong to the show's theatre");
            }

            effectiveScreenId = showDTO.getScreenId();
            screenChanged = effectiveScreenId != show.getScreenId();
        }

//       Check if timings exist in the DTO and are valid
        if (showDTO.getStartTime() != null || showDTO.getEndTime() != null) {
            if (showDTO.getStartTime() == null || showDTO.getEndTime() == null) {
                throw new ShowOperationException("Both startTime and endTime are required");
            }
            if (!showDTO.getStartTime().isBefore(showDTO.getEndTime())) {
                throw new ShowOperationException("startTime must be before endTime");
            }

            effectiveStartTime = showDTO.getStartTime();
            effectiveEndTime = showDTO.getEndTime();
            timingChanged = !effectiveStartTime.equals(show.getStartTime()) || !effectiveEndTime.equals(show.getEndTime());
        }

//        If screen or timing has changed check if it conflicts with existing show!
        if (screenChanged || timingChanged) {
            Optional<Shows> conflict = showRepository.findByScreenIdAndStartTimeLessThanAndEndTimeGreaterThan(
                    effectiveScreenId,
                    effectiveEndTime,
                    effectiveStartTime
            );

            if (conflict.isPresent() && conflict.get().getShowId() != showId) {
                throw new ShowOperationException(
                        "Show conflicts on selected screen and timing with show: " + conflict.get().getShowId()
                );
            }
        }

        if (showDTO.getMovieId() > 0) {
            movieRestClient.getMovie(showDTO.getMovieId());
            show.setMovieId(showDTO.getMovieId());
        }

        if (screenChanged) {
            show.setScreenId(effectiveScreenId);
        }

        if (timingChanged) {
            show.setStartTime(effectiveStartTime);
            show.setEndTime(effectiveEndTime);
        }

        showRepository.save(show);

        return modelMapper.map(show, ShowResponseDTO.class);
    }

    @Transactional
    public void removeShows(long showId) {
        if (showId <= 0) {
            throw new ShowOperationException("Invalid show ID: " + showId);
        }

        showRepository.findByShowId(showId)
                .orElseThrow(() -> new ShowNotFoundException("Show was not found for the given Id!"));

        ticketRestClient.deleteTickets(showId);
        showRepository.deleteByShowId(showId);
    }

//    public boolean isShowConflict(List<Shows> shows,
//                                  LocalDateTime newStart,
//                                  LocalDateTime newEnd,
//                                  long showId) {
//
//        for (Shows existing : shows) {
//
//            // Skip same show during update
//            if (existing.getShowId() == showId) continue;
//
//            LocalDateTime existingStart = existing.getStartTime();
//            LocalDateTime existingEnd   = existing.getEndTime();
//
//            boolean overlap =
//                    existingStart.isBefore(newEnd) &&
//                            existingEnd.isAfter(newStart);
//
//            if (overlap) {
//                return true; // conflict found
//            }
//        }
//        return false; // safe
//    }
}

