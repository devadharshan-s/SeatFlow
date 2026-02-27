package com.example.bookmyshow.user.service;

import com.example.bookmyshow.exception.UserNotFoundException;
import com.example.bookmyshow.services.KeyCloakAdminService;
import com.example.bookmyshow.user.api.dto.UserRequestDTO;
import com.example.bookmyshow.user.api.dto.UserResponseDTO;
import com.example.bookmyshow.user.exception.UserOperationException;
import com.example.bookmyshow.user.model.Role;
import com.example.bookmyshow.user.model.User;
import com.example.bookmyshow.user.repository.RoleRepository;
import com.example.bookmyshow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final KeyCloakAdminService keyCloakAdminService;
    private final RoleRepository roleRepository;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {
        if (request == null) {
            throw new UserOperationException("User payload is required");
        }
        if (request.getUserName() == null || request.getUserName().isBlank()) {
            throw new UserOperationException("Username is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new UserOperationException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new UserOperationException("Password is required");
        }
        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new UserOperationException("Phone is required");
        }

        userRepository.findUserByEmail(request.getEmail()).ifPresent(existing -> {
            throw new UserOperationException("User already exists for email: " + request.getEmail());
        });

        Role defaultRole = roleRepository.findByRoleName("USER");
        if (defaultRole == null) {
            throw new UserOperationException("Default role USER not configured");
        }

        try {
            String keycloakId = keyCloakAdminService.createUser(
                    request.getUserName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getUserName(),
                    "X",
                    request.getPhone()
            );

            if (keycloakId == null || keycloakId.isBlank()) {
                throw new UserOperationException("Failed to create user in Keycloak");
            }

            User user = new User();
            user.setUserName(request.getUserName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setPhone(request.getPhone());
            user.setKeyCloakId(keycloakId);
            user.setRole(defaultRole);

            User savedUser = userRepository.save(user);
            log.info("User '{}' saved in MySQL DB", savedUser.getUserName());
            return toResponse(savedUser);
        } catch (UserOperationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UserOperationException("Failed to create user", ex);
        }
    }

    @Transactional(readOnly = true)
    public Long findUserIdByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new UserOperationException("Email is required");
        }

        return userRepository.findUserByEmail(email)
                .map(User::getUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found for email: " + email));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return ((List<User>) userRepository.findAll()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UserResponseDTO deleteUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new UserOperationException("Email is required to delete user");
        }

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found for email: " + email));

        UserResponseDTO response = toResponse(user);

        try {
            if (user.getKeyCloakId() != null && !user.getKeyCloakId().isBlank()) {
                keyCloakAdminService.deleteUser(user.getKeyCloakId());
            }
            userRepository.delete(user);
            log.info("User '{}' deleted", user.getUserName());
            return response;
        } catch (Exception ex) {
            throw new UserOperationException("Failed to delete user for email: " + email, ex);
        }
    }

    @Transactional
    public UserResponseDTO syncOAuthUser(String keycloakId, String email, String username, List<String> roles) {
        return userRepository.findByKeyCloakId(keycloakId)
                .map(this::toResponse)
                .orElseGet(() -> {
                    User user = new User();
                    user.setKeyCloakId(keycloakId);
                    user.setEmail(email);
                    user.setUserName(username);
                    user.setRole(resolveRoleFromJwt(roles));

                    User saved = userRepository.save(user);
                    log.info("OAuth user synced: {}", email);

                    return toResponse(saved);
                });
    }

    private Role resolveRoleFromJwt(List<String> roles) {
        List<String> safeRoles = roles == null ? List.of() : roles;

        if (safeRoles.contains("ADMIN")) {
            Role admin = roleRepository.findByRoleName("ADMIN");
            if (admin != null) {
                return admin;
            }
        }

        if (safeRoles.contains("THEATRE_MANAGER")) {
            Role manager = roleRepository.findByRoleName("THEATRE_MANAGER");
            if (manager != null) {
                return manager;
            }
        }

        Role userRole = roleRepository.findByRoleName("USER");
        if (userRole == null) {
            throw new UserOperationException("Role USER not configured");
        }
        return userRole;
    }

    private UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setKeyCloakId(user.getKeyCloakId());
        dto.setUsername(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        return dto;
    }
}
