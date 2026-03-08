package org.example.bookmyshowuserservice.user.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Long userId;
    private String keyCloakId;
    private String username;
    private String email;
    private String phone;
    private String role;
}

