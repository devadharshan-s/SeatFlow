package com.example.bookmyshow.services;

import com.example.bookmyshow.DTO.UserResponseDTO;
import com.example.bookmyshow.models.Role;
import com.example.bookmyshow.models.User;
import com.example.bookmyshow.repository.RoleRepository;
import com.example.bookmyshow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @Transactional
    public User createUser(User user) {
        Role customerRole = roleRepository.findById(3);
        try{
            String keyCloakId = keyCloakAdminService.createUser(user.getUserName(), user.getEmail(), user.getPassword(), user.getUserName(), "X", user.getPhone());
//            Matching keycloakid in db
            user.setKeyCloakId(keyCloakId);
//            By Default Customer, can change from backend to ADMIN if needed
            user.setRole(customerRole);
//            Save user
            User savedUser = userRepository.save(user);
            log.info("User '{}' saved in MySQL DB", savedUser.getUserName());
            return savedUser;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public UserResponseDTO findUserByEmail(String email) {
        return modelMapper.map(userRepository.findUserByEmail(email), UserResponseDTO.class);
    }

    @Transactional
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Transactional
    public UserResponseDTO deleteUser(User user) {
        try {
//            The user object we get might only contain email/username,
//            we need to fetch the whole user data from DB.
            User fetchUser = userRepository.findUserByEmail(user.getEmail()).get();
            UserResponseDTO userResponseDTO = modelMapper.map(fetchUser, UserResponseDTO.class);
            keyCloakAdminService.deleteUser(fetchUser.getKeyCloakId());
            userRepository.delete(fetchUser);
            log.info("✔ User '{}' deleted", fetchUser.getUserName());
            return userResponseDTO;
        } catch (Exception e) {
            log.error("❌ Can't delete user! " + e.getMessage());
        }
        return null;
    }

    public UserResponseDTO syncOAuthUser(String keycloakId, String email, String username, List<String> roles){

        return userRepository.findByKeyCloakId(keycloakId)
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .orElseGet(() -> {
                    User user = new User();
                    user.setKeyCloakId(keycloakId);
                    user.setEmail(email);
                    user.setUserName(username);

                    // Role from Keycloak, not hardcoded ID = 3
                    user.setRole(resolveRoleFromJwt(roles));

                    User saved = userRepository.save(user);
                    log.info("✅ OAuth user synced: {}", email);

                    return modelMapper.map(saved, UserResponseDTO.class);
                });
    }

    private Role resolveRoleFromJwt(List<String> roles) {
        if (roles.contains("ADMIN")) {
            return roleRepository.findByRoleName("ADMIN");
        }
        if (roles.contains("THEATRE_MANAGER")) {
            return roleRepository.findByRoleName("THEATRE_MANAGER");
        }
        return roleRepository.findByRoleName("USER");
    }
}
