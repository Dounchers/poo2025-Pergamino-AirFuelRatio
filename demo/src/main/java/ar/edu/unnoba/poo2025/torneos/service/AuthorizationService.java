package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import ar.edu.unnoba.poo2025.torneos.model.Participante;

public interface AuthorizationService {

    /**
     * Valida un token JWT y retorna la instancia del participante asociado.
     * @param token El token JWT (con prefijo "Bearer ").
     * @return La instancia de Participante.
     * @throws Exception Si el token es inválido, expirado o el usuario no existe.
     */
    Participante authorize(String token);
    
    Administrador authorizeAdmin(String token);
}
