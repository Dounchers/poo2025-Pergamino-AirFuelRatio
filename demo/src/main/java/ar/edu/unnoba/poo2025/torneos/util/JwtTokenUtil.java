package ar.edu.unnoba.poo2025.torneos.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JwtTokenUtil {

    // NOTA: En un proyecto real, esta clave secreta debería estar en 
    // application.properties y ser mucho más larga y segura.
    private static final String SECRET_KEY = "clave_secreta_para_poo_2025_torneos";
    private static final long EXPIRATION_DAYS = 10;
    private static final String BEARER_PREFIX = "Bearer ";

    private final Algorithm algorithm;

    public JwtTokenUtil() {
        // Algoritmo HMAC 512
        this.algorithm = Algorithm.HMAC512(SECRET_KEY);
    }

    /**
     * Genera un token JWT para un subject (email).
     *
     * @param subject El email del usuario.
     * @return El token JWT con prefijo "Bearer ".
     */
    public String generateToken(String subject) {
        Instant now = Instant.now();
        Instant expiration = now.plus(EXPIRATION_DAYS, ChronoUnit.DAYS);

        String token = JWT.create()
                .withSubject(subject) // Email
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiration))
                .sign(algorithm);

        return BEARER_PREFIX + token;
    }

    /**
     * Verifica la validez del token (firma y expiración).
     *
     * @param token El token JWT (con o sin prefijo "Bearer ").
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean verify(String token) {
        try {
            String cleanToken = cleanToken(token);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(cleanToken);
            return true;
        } catch (JWTVerificationException e) {
            // El token es inválido (firma incorrecta, expirado, etc.)
            return false;
        }
    }

    /**
     * Retorna el subject (email) del payload del token.
     *
     * @param token El token JWT (con o sin prefijo "Bearer ").
     * @return El subject (email).
     */
    public String getSubject(String token) {
        try {
            String cleanToken = cleanToken(token);
            DecodedJWT jwt = JWT.decode(cleanToken);
            return jwt.getSubject();
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Token inválido al intentar obtener el subject.");
        }
    }

    /**
     * Método privado para quitar el prefijo "Bearer " del token.
     */
    private String cleanToken(String token) {
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }
        return token;
    }
}
