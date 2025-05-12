package com.hotel_ng.app.security.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hotel_ng.app.dto.response.ResponseDTO;
import com.hotel_ng.app.enums.Role;
import com.hotel_ng.app.security.utils.JwtUtils;
import com.hotel_ng.app.service.impl.AdminDetailsServiceImpl;
import com.hotel_ng.app.service.impl.UserDetailsServiceImpl;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final AdminDetailsServiceImpl adminDetailsService;

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/v1/users/auth",
            "/api/v1/admin/auth",
            "/api/v1/rooms/get-all-rooms",
            "/api/v1/rooms/get-types-rooms",
            "/api/v1/rooms/all-available-rooms",
            "/api/v1/rooms/available-rooms-by-date-and-type",
            "/api/v1/bookings/get-by-booking-code",
            "/api/v1/swagger-ui",
            "/api/v1/v3/api-docs",
            "/api/v1/users/form-contact"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String requestPath = request.getRequestURI();
        final String token = getTokenFromRequestHeader(request);

        // Excluir rutas que no requieren autenticación
        if (EXCLUDED_PATHS.stream().anyMatch(requestPath::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Validar el token
        if (token == null) {
            response.sendRedirect("/");
            return;
        }
        if (!JwtUtils.isValidJwtFormat(token)) {
            sendUnauthorizedResponseWithMessage(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "El token no tiene un formato válido");
            return;
        }
        if (JwtUtils.isTokenExpired(token)) {
            sendUnauthorizedResponseWithMessage(response, HttpServletResponse.SC_UNAUTHORIZED, "El token ha expirado");
            return;
        }

        // Extraer información del token
        final String userEmailClaims = JwtUtils.extractUsername(token);
        final String userRoleClaims = JwtUtils.extractRoleToken(token);

        if (userEmailClaims == null || userRoleClaims == null) {
            sendUnauthorizedResponseWithMessage(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "No se pudo extraer la información del token");
            return;
        }

        // Verificar el rol del token
        if (!userRoleClaims.equals(Role.ROLE_USER.name()) && !userRoleClaims.equals(Role.ROLE_ADMIN.name())) {
            sendUnauthorizedResponseWithMessage(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Rol no reconocido en el token");
            return;
        }

        // Cargar UserDetails según el rol
        UserDetails userDetails;
        if (userRoleClaims.equals(Role.ROLE_USER.name())) {
            userDetails = this.userDetailsService.loadUserByUsername(userEmailClaims);
        } else {
            userDetails = this.adminDetailsService.loadUserByUsername(userEmailClaims);
        }

        if (userDetails == null) {
            sendUnauthorizedResponseWithMessage(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "El usuario asociado al token no existe");
            return;
        }

        // Validar el token con los detalles del usuario
        if (!JwtUtils.validateToken(token, userDetails)) {
            sendUnauthorizedResponseWithMessage(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Token inválido o expirado");
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userEmailClaims, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Verificar si el usuario tiene el rol necesario para acceder al endpoint
        if (!hasRequiredRole(requestPath, userDetails)) {
            sendUnauthorizedResponseWithMessage(response, HttpServletResponse.SC_FORBIDDEN,
                    "Acceso denegado: No tienes permisos para realizar esta operación");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequestHeader(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.split(" ")[1];
        }
        return token;
    }

    private boolean hasRequiredRole(String requestPath, UserDetails userDetails) {
        if (
            // habitaciones
                requestPath.startsWith("/api/v1/rooms/create-room") ||
                        requestPath.startsWith("/api/v1/rooms/update-room") ||
                        requestPath.startsWith("/api/v1/rooms/delete-room") ||
                        // usuarios
                        requestPath.startsWith("/api/v1/users/get-user-bookings") ||
                        requestPath.startsWith("/api/v1/users/delete-user") ||
                        requestPath.startsWith("/api/v1/users/get-user") ||
                        requestPath.startsWith("/api/v1/users/all-users") ||
                        //reservas
                        requestPath.startsWith("/api/v1/bookings/all")

        ) {
            // Solo los usuarios con ROLE_ADMIN pueden acceder a estos endpoints
            return userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(Role.ROLE_ADMIN.name()));
        }
        return true;
    }

    private void sendUnauthorizedResponseWithMessage(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        ResponseDTO errorResponse = new ResponseDTO();
        errorResponse.setStatusCode(statusCode);
        errorResponse.setMessage(message);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
