package ar.edu.unnoba.poo2025.torneos.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ar.edu.unnoba.poo2025.torneos.exception.AuthenticationFailedException;
import ar.edu.unnoba.poo2025.torneos.exception.AuthorizationFailedException;
import ar.edu.unnoba.poo2025.torneos.exception.BusinessRuleException;
import ar.edu.unnoba.poo2025.torneos.exception.DuplicateResourceException;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidDateRangeException;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.ResourceNotFoundException;
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
     * Maneja AuthenticationFailedException: Contraseña incorrecta o autenticación fallida
     * Retorna: 401 UNAUTHORIZED
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<?> handleAuthenticationFailed(AuthenticationFailedException e) {
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
     * Maneja MissingRequestHeaderException: Header requerido no presente
     * Retorna: 401 UNAUTHORIZED
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> handleMissingRequestHeader(MissingRequestHeaderException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Autorización requerida"));
    }

    /**
     * Maneja ResourceNotFoundException: Recurso no encontrado
     * Retorna: 404 NOT_FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", e.getMessage()));
    }

    /**
     * Maneja BusinessRuleException: Violación de reglas de negocio
     * Retorna: 400 BAD_REQUEST
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<?> handleBusinessRule(BusinessRuleException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", e.getMessage()));
    }

    /**
     * Maneja DuplicateResourceException: Recurso duplicado
     * Retorna: 409 CONFLICT
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicateResource(DuplicateResourceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("error", e.getMessage()));
    }

    /**
     * Maneja InvalidDateRangeException: Fechas inválidas
     * Retorna: 400 BAD_REQUEST
     */
    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<?> handleInvalidDateRange(InvalidDateRangeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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
