package com.example.bookmyshow.services;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.TheatreDTO.ShowDTO.ShowDTO;
import com.example.bookmyshow.DTO.TheatreDTO.ShowDTO.ShowResponseDTO;
import com.example.bookmyshow.models.Movie.Movie;
import com.example.bookmyshow.models.Theatre.*;
import com.example.bookmyshow.models.Ticket;
import com.example.bookmyshow.repository.*;
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
@RequiredArgsConstructor
@Slf4j
public class ShowService {

    private final ShowsRepository showRepository;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final SeatRepository seatRepository;
    private final TicketRepostiory ticketRepostiory;
    private final ModelMapper modelMapper;

    @Transactional
    public ResponseEntity<ApiResponse<List<ShowResponseDTO>>> getAllShows() {
        try{
            List<Shows> shows = (List<Shows>) showRepository.findAll();

            List<ShowResponseDTO> response = new ArrayList<>();

            for(Shows show : shows){
                ShowResponseDTO showDTO = modelMapper.map(show, ShowResponseDTO.class);
                response.add(showDTO);
            }

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Shows fetched successfully, see below!",
                            response,
                            LocalDateTime.now()
                    ));
        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            500,
                            e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<ShowDTO>> getShowById(long showId){
        try {
            Shows show = showRepository.findByShowId(showId)
                    .orElseThrow(() -> new Exception("show not found for the id " + showId));
            ShowDTO response = modelMapper.map(show, ShowDTO.class);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<ShowDTO>(
                            200,
                            "✅ Show fetched successfully,(Show ID: " + showId + ")" + " see below!",
                            response,
                            LocalDateTime.now()
                    ));

        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<ShowDTO>(
                            400,
                            e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }
//    Should only allow to create shows if movie,seat,screen & theatre exists
    @Transactional
    public ResponseEntity<ApiResponse<ShowDTO>> createShows(ShowDTO show) {
        Shows newShow = new Shows();
        Movie movie;
        Theatre theatre;
        Screen screen;
        try {
            movie = movieRepository.findBymovieId(show.getMovieId())
                    .orElseThrow(() -> new Exception("Movie not found"));
            theatre = theatreRepository.findByTheatreId(show.getTheatreId())
                    .orElseThrow(() -> new Exception("Theatre not found"));
            screen = screenRepository.getScreensByScreenId(show.getScreenId())
                    .orElseThrow(() -> new Exception("Screen Not Found"));
            
            List<Seat> seats = seatRepository.findSeatByScreen_ScreenId(screen.getScreenId());

            if(seats.isEmpty()) {
                throw new Exception("No seats found for screen: " + screen.getScreenName());
            }
            
            if(show.getStartTime() == null) {
                throw new Exception("Please enter a show time");
            }

            for(Shows showsinScreen : screen.getShows()){
                if(showsinScreen.getStartTime().equals(show.getStartTime())) {
                    throw new Exception("Show already exists for this time at screen: " + screen.getScreenName());
                }
            }


            newShow.setScreen(screen);
            newShow.setMovie(movie);
            newShow.setTheatre(theatre);
            newShow.setStartTime(show.getStartTime());
            newShow = showRepository.save(newShow);

            List<Seat> seatsInScreen = seatRepository.findSeatByScreen_ScreenId(newShow.getScreen().getScreenId());


            for(Seat seat : seatsInScreen) {
                ShowSeat showSeat = new ShowSeat();
                showSeat.setShow(newShow);
                showSeat.setSeat(seat);
                showSeat.setIsBooked(false);
                newShow.getShowSeats().add(showSeat);
            }

            newShow = showRepository.save(newShow);

            return ResponseEntity.status(200)
                            .body(new ApiResponse<>(
                                    200,
                                    "✅ Show Created successfully(ShowID) " +   newShow.getShowId() + " on Screen: " + screen.getScreenName() + " & Theatre: "  + theatre.getTheatreName() ,
                                    show,
                                    LocalDateTime.now()
                            ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Show was not created: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<ShowResponseDTO>> updateShow(long showId, ShowDTO showDTO){
        try {
            Shows show = showRepository.findByShowId(showId)
                    .orElseThrow(() -> new Exception("Show not found for the given Id!"));

//            If screen is getting changed
//            Check if the new Screen has bandwidth for the new show!
            if(showDTO.getScreenId() > 0){
                Screen screen = screenRepository.getScreensByScreenId(showDTO.getScreenId())
                        .orElseThrow(() -> new Exception("Screen not found for the given Id!"));
                List<Shows> screenShows = screen.getShows();
                if(isShowConflict(screenShows, showDTO.getStartTime(), showDTO.getEndTime(), showId)) {
                    show.setScreen(screen);
                }
            }
//            If the timing is getting changed
//            Check if the new Start and end Timing is suitable with the screen!
            if(showDTO.getStartTime() != null && showDTO.getEndTime() != null){
                if (isShowConflict(show.getScreen().getShows(), showDTO.getStartTime(), showDTO.getEndTime(), showId)) {
                    show.setStartTime(showDTO.getStartTime());
                }
                else {
                    throw new Exception("Show time is not suitable for the screen!");
                }
            }
            else {
                throw new Exception("Both startTime and endTime are required");
            }

//            if(showDTO.getEndTime() != null){
//                if(isShowConflict(show.getScreen().getShows(), showDTO.getStartTime(), showDTO.getEndTime())){
//                    show.setEndTime(showDTO.getEndTime());
//                } else
//            }

//            If the movie's getting changed
//            Check the movie exists in db or not
            if(showDTO.getMovieId() > 0){
                Movie movie = movieRepository.findBymovieId(showDTO.getMovieId())
                        .orElseThrow(() -> new Exception("Movie not found for the given Id!"));

                show.setMovie(movie);
            }

            showRepository.save(show);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Show updated successfully!",
                            modelMapper.map(show, ShowResponseDTO.class),
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Show was not updated due to: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> removeShows(long showId){
        try {
            Shows show = showRepository.findByShowId(showId)
                    .orElseThrow(() -> new Exception("Show was not found for the given Id!"));
            List<Ticket> tickets = ticketRepostiory.findAllByShow_ShowId(showId)
                    .orElseThrow(() -> new Exception("No tickets found for the show, still cancelling the show"));
            List<Long> ticketIds = tickets.stream().map(Ticket::getTicketId).toList();
            ticketRepostiory.deleteTickets(ticketIds);
            showRepository.deleteByShowId(showId);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ The Show at" + show.getStartTime().toString()
                                    + " for movie " + show.getMovie()
                            + " at theatre " + show.getTheatre().getTheatreName()
                            + " has been deleted successfully.",
                            null,
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Show was not deleted due to: " +  e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    public boolean isShowConflict(List<Shows> shows,
                                  LocalDateTime newStart,
                                  LocalDateTime newEnd,
                                  long showId) {

        for (Shows existing : shows) {

            // Skip same show during update
            if (existing.getShowId() == showId) continue;

            LocalDateTime existingStart = existing.getStartTime();
            LocalDateTime existingEnd   = existing.getEndTime();

            boolean overlap =
                    existingStart.isBefore(newEnd) &&
                            existingEnd.isAfter(newStart);

            if (overlap) {
                return true; // conflict found
            }
        }
        return false; // safe
    }
}
