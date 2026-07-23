package org.example.bookmyshowbookingservice.booking.client.impl;

import org.example.bookmyshowbookingservice.booking.client.UserClient;
import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public ApiResponse<Long> getUserByEmail(String email) {

        return new ApiResponse<>(
                503,
                "Fallback: Unable to retrieve user ID for email: " + email,
                null,
                LocalDateTime.now()
        );

    }
}
