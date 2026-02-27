package com.example.bookmyshow.booking.client.impl;

import com.example.bookmyshow.booking.client.UserClient;
import com.example.bookmyshow.booking.exception.BookingFailedException;
import com.example.bookmyshow.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class UserRestClient implements UserClient {

    private final RestClient restClient;

    @Override
    public Long getUserIdByEmail(String email) {
        try {
            ApiResponse<Long> response = restClient.get()
                    .uri("/getUser?email={email}", email)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if(response == null || response.getData() == null){
                throw new Exception("User Not found!");
            }

            return response.getData();
        } catch (Exception ex) {
            throw new BookingFailedException(
                    "User lookup failed for email: " + email,
                    ex
            );
        }
    }
}




