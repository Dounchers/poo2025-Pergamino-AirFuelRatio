package ar.edu.unnoba.poo2025.torneos.repository;

import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    
    // Listar inscripciones de una competencia
    List<Inscripcion> findByCompetenciaId(Long competenciaId);

    // Contar inscripciones
    long countByCompetenciaId(Long competenciaId);

    // Sumar recaudación (maneja null si no hay inscripciones)
    @Query("SELECT COALESCE(SUM(i.price), 0) FROM Inscripcion i WHERE i.competencia.id = :competenciaId")
    BigDecimal sumPriceByCompetenciaId(@Param("competenciaId") Long competenciaId);
}
