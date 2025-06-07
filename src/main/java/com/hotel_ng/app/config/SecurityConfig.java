package com.hotel_ng.app.config;

import com.hotel_ng.app.security.jwt.JwtAuthenticationFilter;
import com.hotel_ng.app.service.interfaces.IUserDetailsService;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hotel_ng.app.enums.Role;


import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final IUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter authenticationFilter;


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .authorizeHttpRequests(http -> http
                        // Administradores
                        .requestMatchers(HttpMethod.POST, "/admin/auth/**").permitAll()

                        // usuarios
                        .requestMatchers(HttpMethod.POST, "/users/form-contact").permitAll()

                        .requestMatchers(HttpMethod.POST, "/users/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/all-users")
                        .hasAuthority(Role.ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.GET, "/users/get-user/**")
                        .hasAuthority(Role.ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.GET, "/users/get-profile-user-info").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/get-user-bookings/**")
                        .hasAuthority(Role.ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.GET, "/users/delete-user/**")
                        .hasAuthority(Role.ROLE_ADMIN.name())

                        // reservas
                        .requestMatchers(HttpMethod.POST, "/bookings/new-reservation/book-room/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/bookings/all")
                        .hasAuthority(Role.ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.DELETE, "/bookings/cancel-booking")
                        .hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name())

                        .requestMatchers(HttpMethod.GET, "/bookings/get-by-booking-code/**").permitAll()

                        // habitaciones
                        .requestMatchers(HttpMethod.GET, "/rooms/get-all-rooms").permitAll()
                        .requestMatchers(HttpMethod.GET, "/rooms/all-available-rooms").permitAll()
                        .requestMatchers(HttpMethod.GET, "/rooms/get-types-rooms").permitAll()
                        .requestMatchers(HttpMethod.GET, "/rooms/available-rooms-by-date-and-type").permitAll()
                        .requestMatchers(
                                "/rooms/create-room",
                                "/rooms/update-room/**",
                                "/rooms/delete-room/**")
                        .hasAuthority(Role.ROLE_ADMIN.name())
                        // pruebas con swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // autenticados
                        .anyRequest().authenticated())

                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterAfter(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    // Authentication manager(toda solicitud pasa por este filtro)
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
