package org.example.bookmyshowuserservice.user.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    private String userName;
    private String email;
    private String phone;
    private String password;
}

