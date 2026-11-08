package com.inventario.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetterAuthTokenValidator {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Valida el token contra la base de datos de Better Auth (PostgreSQL)
     * Soporta tanto el Session Token directo como tokens JWT.
     */
    public Optional<AuthenticatedUser> validateToken(String rawToken) {
        if (rawToken == null || rawToken.trim().isEmpty()) {
            return Optional.empty();
        }

        String token = rawToken.trim();

        // 1. Si es un JWT (formato header.payload.signature), intentamos extraer el sub o sessionToken
        if (token.startsWith("eyJ") && token.contains(".")) {
            Optional<AuthenticatedUser> jwtUser = validateJwtPayload(token);
            if (jwtUser.isPresent()) {
                return jwtUser;
            }
        }

        // 2. Validación estándar contra la tabla session de Better Auth
        return validateSessionToken(token);
    }

    private Optional<AuthenticatedUser> validateSessionToken(String token) {
        String sql = """
            SELECT u.id, u.email, u.name, u.role, u.banned, s.active_organization_id 
            FROM session s 
            JOIN "user" u ON s.user_id = u.id 
            WHERE s.token = ? AND s.expires_at > CURRENT_TIMESTAMP
            """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                boolean banned = rs.getBoolean("banned");
                if (banned) {
                    log.warn("Usuario con sesión activa está baneado: {}", rs.getString("email"));
                    return Optional.<AuthenticatedUser>empty();
                }

                String role = rs.getString("role");
                if (role == null || role.isBlank()) {
                    role = "user";
                }

                return Optional.of(AuthenticatedUser.builder()
                        .id(rs.getString("id"))
                        .email(rs.getString("email"))
                        .name(rs.getString("name"))
                        .role(role)
                        .activeOrganizationId(rs.getString("active_organization_id"))
                        .build());
            }, token);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Token de sesión no encontrado o expirado: {}", token);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error al validar token de sesión en PostgreSQL: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<AuthenticatedUser> validateJwtPayload(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\.");
            if (parts.length < 2) return Optional.empty();

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

            // Si el payload contiene un sub (ID de usuario de Better Auth)
            if (payloadJson.contains("\"sub\":")) {
                String sub = extractJsonValue(payloadJson, "sub");
                if (sub != null && !sub.isBlank()) {
                    String userSql = "SELECT id, email, name, role, banned FROM \"user\" WHERE id = ?";
                    return jdbcTemplate.queryForObject(userSql, (rs, rowNum) -> {
                        if (rs.getBoolean("banned")) return Optional.<AuthenticatedUser>empty();
                        String role = rs.getString("role");
                        return Optional.of(AuthenticatedUser.builder()
                                .id(rs.getString("id"))
                                .email(rs.getString("email"))
                                .name(rs.getString("name"))
                                .role(role != null ? role : "user")
                                .build());
                    }, sub);
                }
            }
        } catch (Exception e) {
            log.debug("No se pudo decodificar JWT directamente, intentando como session token: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }
}
