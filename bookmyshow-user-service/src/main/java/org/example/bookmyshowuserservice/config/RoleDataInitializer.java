package org.example.bookmyshowuserservice.config;

import lombok.RequiredArgsConstructor;
import org.example.bookmyshowuserservice.user.model.Role;
import org.example.bookmyshowuserservice.user.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RoleDataInitializer {

    private final RoleRepository roleRepository;

    @Bean
    CommandLineRunner seedRoles() {
        return args -> {
            ensureRole("USER");
            ensureRole("ADMIN");
            ensureRole("THEATRE_MANAGER");
        };
    }

    private void ensureRole(String roleName) {
        roleRepository.findByRoleName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setRoleName(roleName);
            return roleRepository.save(role);
        });
    }
}

