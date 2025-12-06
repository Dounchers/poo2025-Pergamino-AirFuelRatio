package ar.edu.unnoba.poo2025.torneos.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscripcion, Long> {
    // Método para la regla: Única vez por competencia
    boolean existsByParticipanteIdAndCompetenciaId(Long participanteId, Long competenciaId);

    // Método para la regla: Descuento 50%
    @Query("SELECT COUNT(i) > 0 FROM Inscripcion i " +
            "WHERE i.participante.id = :participanteId " +
            "AND i.competencia.torneo.id = :torneoId " +
            "AND i.competencia.id <> :excludedCompetitionId")
    boolean existsByParticipanteIdAndTorneoIdExcludingCompetition(
            @Param("participanteId") Long participanteId,
            @Param("torneoId") Long torneoId,
            @Param("excludedCompetitionId") Long excludedCompetitionId);

    // Consulta más eficiente que carga las relaciones necesarias para evitar el problema N+1
    @Query("SELECT i FROM Inscripcion i JOIN FETCH i.competencia c JOIN FETCH c.torneo t WHERE i.participante.id = :participanteId")
    List<Inscripcion> findInscriptionsWithDetailsByParticipanteId(@Param("participanteId") Long participanteId);

    // Consulta para detalle y seguridad IDOR
    @Query("SELECT i FROM Inscripcion i JOIN FETCH i.competencia c JOIN FETCH c.torneo t " +
            "WHERE i.id = :inscriptionId AND i.participante.id = :participanteId")
    Optional<Inscripcion> findByIdAndParticipanteIdWithDetails(
            @Param("inscriptionId") Long inscriptionId,
            @Param("participanteId") Long participanteId);

    // Listar inscripciones de una competencia
    List<Inscripcion> findByCompetenciaId(Long competenciaId);

    // Contar inscripciones
    long countByCompetenciaId(Long competenciaId);

    // Sumar recaudación (maneja null si no hay inscripciones)
    @Query("SELECT COALESCE(SUM(i.price), 0) FROM Inscripcion i WHERE i.competencia.id = :competenciaId")
    BigDecimal sumPriceByCompetenciaId(@Param("competenciaId") Long competenciaId);
}
