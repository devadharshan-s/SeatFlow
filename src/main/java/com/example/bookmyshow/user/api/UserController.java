package com.example.bookmyshow.user.api;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.user.api.dto.UserRequestDTO;
import com.example.bookmyshow.user.api.dto.UserResponseDTO;
import com.example.bookmyshow.user.service.UserService;
import lombok.RequiredArgsConstructor;
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
public class UserController {

    private final UserService userService;

    @PostMapping("/createUser")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO response = userService.createUser(userRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "User created successfully",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/getUser")
    public ResponseEntity<ApiResponse<Long>> getUser(@RequestParam String email) {
        Long userId = userService.findUserIdByEmail(email);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "User fetched successfully",
                        userId,
                        LocalDateTime.now()
                )
        );
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<ApiResponse<UserResponseDTO>> deleteUser(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO response = userService.deleteUserByEmail(userRequestDTO.getEmail());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "User deleted successfully",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> syncOAuthUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Authentication required",
                            null,
                            LocalDateTime.now()
                    )
            );
        }

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
                        "User synced successfully",
                        user,
                        LocalDateTime.now()
                )
        );
    }
}
