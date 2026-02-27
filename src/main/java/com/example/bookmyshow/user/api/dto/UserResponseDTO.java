package com.example.bookmyshow.user.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private String keyCloakId;
    private String username;
    private String email;
    private String phone;
}
