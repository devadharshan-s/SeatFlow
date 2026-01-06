package com.example.bookmyshow.services;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.TicketDTO;
import com.example.bookmyshow.models.Theatre.ShowSeat;
import com.example.bookmyshow.models.Theatre.Shows;
import com.example.bookmyshow.models.Ticket;
import com.example.bookmyshow.models.User;
import com.example.bookmyshow.repository.ShowSeatRepository;
import com.example.bookmyshow.repository.ShowsRepository;
import com.example.bookmyshow.repository.TicketRepostiory;
import com.example.bookmyshow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepostiory ticketRepostiory;
    private final ShowsRepository showsRepository;
    private final ShowSeatRepository showSeatRepository;
    private final UserRepository userRepository;
    private final LockService lockService;
    private final ModelMapper modelMapper;

    public static Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Jwt) auth.getPrincipal();
    }

    @Transactional
    public ResponseEntity<ApiResponse<TicketDTO>> bookTicket(TicketDTO ticketDTO) {
        try {
//          Check if the show exists or not
            Shows checkShow = showsRepository.findByShowId(ticketDTO.getShowId())
                    .orElseThrow(() -> new Exception("Show not found!"));
            List<Long> seatIds = ticketDTO.getSeatIds();
            List<ShowSeat> showSeats = showSeatRepository.findShowSeats(checkShow, seatIds);

            if(showSeats.size() != seatIds.size()) {
                throw new Exception("Check entered Seat Id's, Some Seat numbers don't match!");
            }

            boolean locked = lockService.lockSeats(showSeats, 300);

            if(!locked) {
                throw new Exception("Seat's are locked already or someone booked it!");
            }

//            Fetching users name before saving the ticket.
            User user = userRepository.findUserByEmail(getJwt().getClaim("email"))
                    .orElseThrow(() -> new Exception("User not found "));

            Ticket ticket = new  Ticket();
            ticket.setShow(checkShow);
            ticket.setUser(user);

            ticket = ticketRepostiory.save(ticket);

//            Release lock for seats

            for(ShowSeat showSeat : showSeats) {
                showSeat.setTicket(ticket);
                showSeat.setIsBooked(Boolean.TRUE);
                showSeat.setLockedUntil(null);
            }

            showSeatRepository.saveAll(showSeats);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<TicketDTO>(
                            200,
                            "✔ Seats booked Successfully!, Ticket Id: " + ticket.getTicketId(),
                            ticketDTO,
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<TicketDTO>(
                            400,
                            "❌ Seat booking failed!",
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<TicketDTO>> cancelTicket(long ticketId) {
        try {
            Ticket ticket = ticketRepostiory.findById(ticketId)
                    .orElseThrow(() -> new Exception("Ticket not found, Check ticket Id!"));

//            Mark the booked seats as free
            List<ShowSeat> bookedSeats = showSeatRepository.findByTicket(ticket)
                    .orElseThrow(() -> new Exception("Seats related to ticket are not found!"));

            for(ShowSeat seat : bookedSeats) {

                seat.setIsBooked(Boolean.FALSE);
                seat.setTicket(null);
                seat.setLockedUntil(null);
            }

            showSeatRepository.saveAll(bookedSeats);

            TicketDTO response = new TicketDTO();
            response.setShowId(ticket.getShow().getShowId());

            List<Long> seatIds = bookedSeats.stream().map(
                    seat -> seat.getSeat().getSeatId()
            ).toList();

            response.setSeatIds(seatIds);
            ticketRepostiory.deleteById(ticketId);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<TicketDTO>(
                       200,
                       "✔ Ticket Cancelled Successfully",
                        response,
                        LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(400)
                    .body(new ApiResponse<TicketDTO>(
                            400,
                            "❌ Please check if ticket exists!",
                            null,
                            LocalDateTime.now()
                    ));
        }
    }
}
