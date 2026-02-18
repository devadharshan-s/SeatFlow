package com.example.bookmyshow.booking.client.impl;

import com.example.bookmyshow.booking.client.SeatClient;
import com.example.bookmyshow.booking.exception.BookingFailedException;
import com.example.bookmyshow.booking.exception.TicketCancellationException;
import com.example.bookmyshow.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component("bookingSeatRestClient")
@RequiredArgsConstructor
public class SeatRestClient implements SeatClient {

    private final RestClient restClient;

    @Override
    public List<Long> getSeats(Long showId, List<Long> seatIds) {
        try {
            ApiResponse<List<Long>> response = restClient.post()
                    .uri("/getShowSeats/{showId}", showId)
                    .body(seatIds)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if(response == null || response.getData() == null){
                throw new Exception("No seats found!");
            }

            return response.getData();
        } catch (Exception ex) {
            throw new BookingFailedException(
                    "Seat reservation failed for showId: " + showId,
                    ex
            );
        }
    }

    @Override
    public List<Long> lockSeats(List<Long> seatIds, int seconds){
        try {
            ApiResponse<List<Long>> response = restClient.post()
                    .uri("/lockSeats/{seconds}", seconds)
                    .body(seatIds)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if(response == null || response.getData() == null){
                throw new BookingFailedException("Can't lock seats, check lock service!");
            }

            return response.getData();
        } catch (Exception ex) {
            throw new BookingFailedException(
                    "Seat locking failed for seatIds: " + seatIds,
                    ex
            );
        }
    }

    public List<Long> bookSeats(List<Long> seatIds, long ticketId){
        try {
            return restClient.post()
                    .uri("/bookSeats/{ticketId}", ticketId)
                    .body(seatIds)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception ex) {
            throw new BookingFailedException(
                    "Seat booking failed for seatIds: " + seatIds,
                    ex
            );
        }
    }

    @Override
    public void unlockSeats(Long ticketId, List<Long> seatIds) {
        try {
            restClient.post()
                    .uri("/unlockSeats/{ticketId}", ticketId)
                    .body(seatIds)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            throw new BookingFailedException(
                    "Seat unlock failed for ticketId: " + ticketId,
                    ex
            );
        }
    }

    @Override
    public List<Long> cancelSeats(Long ticketId) {
        try {
             ApiResponse<List<Long>> response = restClient.delete()
                    .uri("/cancelSeats/{ticketId}", ticketId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

             if(response == null || response.getData() == null){
                 throw new TicketCancellationException("Can't cancel seats, check cancel service!");
             }

            return response.getData();
        } catch (Exception ex) {
            throw new TicketCancellationException(
                    ticketId.toString(), ex
            );
        }
    }
}
