package ar.edu.unnoba.poo2025.torneos.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.unnoba.poo2025.torneos.exception.AuthorizationFailedException;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;
import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import ar.edu.unnoba.poo2025.torneos.service.AuthorizationService;

/**
 * Validador centralizado para autenticación y autorización de administradores.
 * Encapsula la lógica de validación de tokens para reutilización en múltiples recursos.
 */
@Component
public class AdminValidator {

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * Valida el token de un administrador.
     * 
     * @param token Token JWT del administrador
     * @return Administrador validado
     * @throws AuthorizationFailedException Si el token es nulo o vacío
     * @throws InvalidTokenException Si el token es inválido o expirado
     * @throws UserNotFoundException Si el administrador no existe en la BD
     */
    public Administrador validate(String token) {
        if (token == null || token.isEmpty()) {
            throw new AuthorizationFailedException("Token requerido");
        }
        return authorizationService.authorizeAdmin(token);
    }
}
