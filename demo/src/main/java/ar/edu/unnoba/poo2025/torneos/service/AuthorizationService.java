package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import ar.edu.unnoba.poo2025.torneos.model.Participante;

public interface AuthorizationService {

    /**
     * Valida un token JWT y retorna la instancia del participante asociado.
     * @param token El token JWT (con prefijo "Bearer ").
     * @return La instancia de Participante.
     * @throws InvalidTokenException Si el token es inválido o expirado.
     * @throws UserNotFoundException Si el usuario asociado al token no existe.
     */
    Participante authorize(String token);
    
    /**
     * Valida un token JWT de administrador y retorna la instancia asociada.
     * @param token El token JWT del administrador.
     * @return La instancia de Administrador.
     * @throws InvalidTokenException Si el token es inválido o expirado.
     * @throws UserNotFoundException Si el administrador asociado al token no existe.
     */
    Administrador authorizeAdmin(String token);
}
