package ar.edu.unnoba.poo2025.torneos.repository;

import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Administrador, Long> {
    Administrador findByEmail(String email);
}
