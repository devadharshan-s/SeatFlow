package org.example.bookmyshowuserservice.user.service;

import lombok.RequiredArgsConstructor;
import org.example.bookmyshowuserservice.common.exception.UserNotFoundException;
import org.example.bookmyshowuserservice.user.api.dto.UserRequestDTO;
import org.example.bookmyshowuserservice.user.api.dto.UserResponseDTO;
import org.example.bookmyshowuserservice.user.exception.UserOperationException;
import org.example.bookmyshowuserservice.user.model.Role;
import org.example.bookmyshowuserservice.user.model.User;
import org.example.bookmyshowuserservice.user.repository.RoleRepository;
import org.example.bookmyshowuserservice.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()) {
            throw new UserOperationException("Email is required");
        }
        if (request.getUserName() == null || request.getUserName().isBlank()) {
            throw new UserOperationException("Username is required");
        }

        userRepository.findUserByEmail(request.getEmail()).ifPresent(existing -> {
            throw new UserOperationException("User already exists for email: " + request.getEmail());
        });

        Role defaultRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new UserOperationException("Default role USER not configured"));

        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        user.setRole(defaultRole);

        return toResponse(userRepository.save(user));
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
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public UserResponseDTO deleteUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new UserOperationException("Email is required to delete user");
        }

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found for email: " + email));

        UserResponseDTO response = toResponse(user);
        userRepository.delete(user);
        return response;
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
                    return toResponse(userRepository.save(user));
                });
    }

    private Role resolveRoleFromJwt(List<String> roles) {
        List<String> safeRoles = roles == null ? List.of() : roles;
        if (safeRoles.contains("ADMIN")) {
            return roleRepository.findByRoleName("ADMIN").orElseGet(() -> createRole("ADMIN"));
        }
        if (safeRoles.contains("THEATRE_MANAGER")) {
            return roleRepository.findByRoleName("THEATRE_MANAGER").orElseGet(() -> createRole("THEATRE_MANAGER"));
        }
        return roleRepository.findByRoleName("USER").orElseGet(() -> createRole("USER"));
    }

    private Role createRole(String roleName) {
        Role role = new Role();
        role.setRoleName(roleName);
        return roleRepository.save(role);
    }

    private UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setKeyCloakId(user.getKeyCloakId());
        dto.setUsername(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole() != null ? user.getRole().getRoleName() : null);
        return dto;
    }
}

