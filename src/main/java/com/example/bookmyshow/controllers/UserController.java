package com.example.bookmyshow.controllers;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.UserRequestDTO;
import com.example.bookmyshow.DTO.UserResponseDTO;
import com.example.bookmyshow.models.User;
import com.example.bookmyshow.services.KeyCloakAdminService;
import com.example.bookmyshow.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final KeyCloakAdminService keyCloakAdminService;
    private final ModelMapper modelMapper;

    @PostMapping("/createUser")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        User user = modelMapper.map(userRequestDTO, User.class);
        try {
            userService.createUser(user);
            UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
            ApiResponse response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "✅ User created Successfully",
                    userResponseDTO,
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Failed Creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Can't create user: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @GetMapping("/getUser")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUser(@RequestParam String email) {
        UserResponseDTO response = userService.findUserByEmail(email);
        if(response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        "User not found",
                        null,
                        LocalDateTime.now()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.FOUND.value(),
                            "✅ User found Successfully",
                            response,
                            LocalDateTime.now()
                    ));
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<ApiResponse<UserResponseDTO>> deleteUser(@RequestBody UserRequestDTO userRequestDTO) {
        User user = modelMapper.map(userRequestDTO, User.class);
        try{
            UserResponseDTO userResponseDTO = userService.deleteUser(user);
            ApiResponse<UserResponseDTO> apiResponse = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "🗑️ User deleted Succesfully",
                    userResponseDTO,
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (Exception e){
            log.error("Failed Deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "User deletion failed! " + e.getMessage(),
                            null,
                            LocalDateTime.now()
            ));
        }
     }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> syncOAuthUser(@AuthenticationPrincipal Jwt jwt){

        String email = jwt.getClaim("email");
        String keyCloakId = jwt.getSubject();
        String username = jwt.getClaim("preferred_username");

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        List<String> roles = realmAccess != null
                ? (List<String>) realmAccess.get("roles")
                : List.of();

        UserResponseDTO user = userService.syncOAuthUser(
                keyCloakId,
                email,
                username,
                roles
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "✅ User synced successfully",
                        user,
                        LocalDateTime.now()
                )
        );
    }
}
