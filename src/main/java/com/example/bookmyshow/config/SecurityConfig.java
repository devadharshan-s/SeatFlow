package com.example.bookmyshow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakConverter());

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 🔓 Public
                        .requestMatchers(
                                "/createUser/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/**",
                                "/me"
                        ).permitAll()

                        // 🔴 ADMIN only
                        .requestMatchers(
                                "/deleteMovie/**",
                                "/deletePerson/**",
                                "/deleteTheatre",
                                "/createTheatre"
                        ).hasRole("ADMIN")

                        // 🟡 THEATRE MANAGER
                        .requestMatchers(
                                "/updateTheatre/**",
                                "/createScreen",
                                "/updateScreen/**",
                                "/createSeats",
                                "/updateSeat/**",
                                "/createShow",
                                "/updateShow/**",
                                "/deleteShow"
                        ).hasAnyRole("ADMIN", "THEATRE_MANAGER")

                        // 🟢 USER
                        .requestMatchers(
                                "/selectSeats/**",
                                "/bookTickets"
                        ).hasRole("USER")

                        // 🔐 Everything else needs auth
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                );

        return http.build();
    }

//    @Bean
//    @Order(1)
//    public SecurityFilterChain publicEndpoints(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher(
//                        "/createUser/**",
//                        "/actuator/**",
//                        "/v3/api-docs/**",
//                        "/swagger-ui/**",
//                        "/swagger-ui.html"
//                )
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//
//        return http.build();
//    }
//
//    @Bean
//    @Order(2)
//    public SecurityFilterChain protectedEndpoints(HttpSecurity http) throws Exception {
//
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakConverter());
//
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(t ->
//                        t.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authorizeHttpRequests(auth ->
//                        auth.anyRequest().authenticated()
//                )
//                .oauth2ResourceServer(oauth ->
//                        oauth.jwt(jwt ->
//                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
//                        )
//                );
//
//        return http.build();
//    }
}




