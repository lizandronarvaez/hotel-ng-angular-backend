package com.hotel_ng.app.security.utils;

import java.util.*;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;

@Service
public class JwtUtils {

    // Tiempo expiration token
    private static final long EXPIRES_TIME = 1000 * 600 * 24;
    // Generar la llave secreta
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Claims claims = Jwts.claims().add("authorities", roles).build();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRES_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String extractUsername(String token) {
        return getTokenBody(token).getSubject();
    }

    public static String extractRoleToken(String token) {
        Claims claims = getTokenBody(token);
        List<String> authorities = claims.get("authorities", List.class);
    
        if (authorities != null && !authorities.isEmpty()) {
            return authorities.get(0);
        }
        return null;
    }

    // Valida el token
    public static Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Obtiene el token del request y lo verify
    private static Claims getTokenBody(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            throw new AccessDeniedException("Acceso denegado: El token ha expirado");
        } catch (SignatureException ex) {
            throw new AccessDeniedException("Acceso denegado: Firma del token inválida");
        } catch (MalformedJwtException ex) {
            throw new AccessDeniedException("Acceso denegado: Token malformado");
        } catch (UnsupportedJwtException ex) {
            throw new AccessDeniedException("Acceso denegado: Token no soportado");
        } catch (IllegalArgumentException ex) {
            throw new AccessDeniedException("Acceso denegado: Token inválido");
        }
    }

    // Comprueba que el token no haya expirado
    public static boolean isTokenExpired(String token) {
        Claims claims = getTokenBody(token);
        return claims.getExpiration().before(new Date());
    }

    public static boolean isValidJwtFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        // Un JWT válido tiene 3 partes separadas por puntos: header.payload.signature
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }
}
