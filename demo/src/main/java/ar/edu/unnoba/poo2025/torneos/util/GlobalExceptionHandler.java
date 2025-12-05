package ar.edu.unnoba.poo2025.torneos.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ar.edu.unnoba.poo2025.torneos.exception.AuthorizationFailedException;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;

/**
 * Manejador global de excepciones para toda la aplicación
 * Centraliza el manejo de excepciones de autenticación y autorización
 * Captura excepciones lanzadas en cualquier @RestController o @Service
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja InvalidTokenException: Token inválido o expirado
     * Retorna: 401 UNAUTHORIZED
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", e.getMessage()));
    }

    /**
     * Maneja UserNotFoundException: Usuario no existe (pero token es válido)
     * Retorna: 401 UNAUTHORIZED
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", e.getMessage()));
    }

    /**
     * Maneja AuthorizationFailedException: Token nulo o vacío
     * Retorna: 401 UNAUTHORIZED
     */
    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<?> handleAuthorizationFailed(AuthorizationFailedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", e.getMessage()));
    }

    /**
     * Maneja cualquier otra excepción no capturada
     * Retorna: 500 INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Error interno del servidor"));
    }
}
