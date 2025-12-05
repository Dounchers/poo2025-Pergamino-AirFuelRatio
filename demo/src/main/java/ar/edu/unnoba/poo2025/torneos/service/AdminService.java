package ar.edu.unnoba.poo2025.torneos.service;

import java.util.List;

import ar.edu.unnoba.poo2025.torneos.model.Administrador;

public interface AdminService {

    /**
     * Busca un administrador por email.
     * @param email El email del administrador.
     * @return La instancia de Administrador o null si no existe.
     */
    Administrador findByEmail(String email) ;

    /**
     * Crea un nuevo administrador.
     * @param administrador La instancia de Administrador a crear.
     * @return El administrador creado con el password hasheado.
     * @throws Exception Si ya existe un administrador con el mismo email.
     */
    Administrador create(Administrador administrador);

    List<Administrador> findAll();

    void delete(Long id);

    Administrador findById(Long id) throws Exception;
}
