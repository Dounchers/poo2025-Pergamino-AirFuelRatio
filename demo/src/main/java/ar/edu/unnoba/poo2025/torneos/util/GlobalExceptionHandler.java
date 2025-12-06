package ar.edu.unnoba.poo2025.torneos.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ar.edu.unnoba.poo2025.torneos.exception.AuthorizationFailedException;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;
import ar.edu.unnoba.poo2025.torneos.exception.*;

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

    /**
     * Excepciones para recursos no encontrados
     * Retorna: 404 NOT_FOUND
     */

    @ExceptionHandler({
            ResourceNotFoundException.class,
            TournamentNotFoundException.class,
            CompetitionNotFoundException.class,
            ParticipantNotFoundException.class
    })
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404
                .body(Map.of("error", ex.getMessage()));
    }

    //** Excepciones para recursos conflictivos
    // * Retorna: 401 CONFLICT
    //

    @ExceptionHandler({
            AlreadyInscribedException.class,
            EmailAlreadyRegisteredException.class,
            DocumentAlreadyRegisteredException.class
    })
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT) // 409
                .body(Map.of("error", ex.getMessage()));
    }
    //** Excepciones para solicitudes incorrectas
    // * Retorna: 400 BAD_REQUEST
    //
    @ExceptionHandler({
            NoCapacityException.class,
            TournamentNotPublishedException.class,
            EnrollmentDateExceededException.class,
            TournamentAlreadyPublishedException.class
    })
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                .body(Map.of("error", ex.getMessage()));
    }

    //* Excepcion para no autorizado
    //* Retorna: 403 FORBIDDEN
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403
                .body(Map.of("error", ex.getMessage()));
    }

}