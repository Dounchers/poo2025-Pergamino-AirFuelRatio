package ar.edu.unnoba.poo2025.torneos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ar.edu.unnoba.poo2025.torneos.model.Administrador;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
   
    @Query("SELECT a FROM Administrador a WHERE a.email = :email")
    Administrador findByEmail(@Param("email") String email);
    // save(), findAll() y deleteById() ya están incluidos en JpaRepository
}