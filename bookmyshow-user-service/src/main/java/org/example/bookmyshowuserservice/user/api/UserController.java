package org.example.bookmyshowuserservice.user.api;

import lombok.RequiredArgsConstructor;
import org.example.bookmyshowuserservice.common.dto.ApiResponse;
import org.example.bookmyshowuserservice.user.api.dto.UserRequestDTO;
import org.example.bookmyshowuserservice.user.api.dto.UserResponseDTO;
import org.example.bookmyshowuserservice.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                new ApiResponse<>(HttpStatus.CREATED.value(), "User created successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping("/getUser")
    public ResponseEntity<ApiResponse<Long>> getUser(@RequestParam String email) {
        Long userId = userService.findUserIdByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>(200, "User fetched successfully", userId, LocalDateTime.now()));
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<ApiResponse<UserResponseDTO>> deleteUser(@RequestParam String email) {
        UserResponseDTO response = userService.deleteUserByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>(200, "User deleted successfully", response, LocalDateTime.now()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> syncOAuthUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Authentication required", null, LocalDateTime.now()));
        }

        String email = jwt.getClaim("email");
        String keycloakId = jwt.getSubject();
        String username = jwt.getClaim("preferred_username");
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        List<String> roles = realmAccess != null ? (List<String>) realmAccess.get("roles") : List.of();

        UserResponseDTO user = userService.syncOAuthUser(keycloakId, email, username, roles);
        return ResponseEntity.ok(new ApiResponse<>(200, "User synced successfully", user, LocalDateTime.now()));
    }
}

