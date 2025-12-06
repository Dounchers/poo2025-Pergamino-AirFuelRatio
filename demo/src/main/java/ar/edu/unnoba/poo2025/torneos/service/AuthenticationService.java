package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import ar.edu.unnoba.poo2025.torneos.model.Participante;

public interface AuthenticationService {

    /**
     * Autentica a un participante y genera un token JWT.
     * @param participant La instancia de Participante con email y password plano.
     * @return El token JWT (con prefijo "Bearer ").
     * @throws Exception Si las credenciales son inválidas o el usuario no existe.
     */
    public String authenticate(Participante participant) throws Exception;

    /**
     * Autentica a un administrador y genera un token JWT.
     * @param administrador La instancia de Administrador con email y password plano.
     * @return El token JWT (con prefijo "Bearer ").
     * @throws Exception Si las credenciales son inválidas o el usuario no existe.
     */
    public String authenticate(Administrador administrador) throws Exception;
}
