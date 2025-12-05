package ar.edu.unnoba.poo2025.torneos.service;

import java.util.List;
import ar.edu.unnoba.poo2025.torneos.dto.CompetitionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;

public interface CompetitionService {
    // Métodos de Consulta Pública
    List<CompetitionResponseDTO> findByTournamentId(Long tournamentId) throws Exception;
    CompetitionResponseDTO findByIdAndTorneoId(Long competitionId, Long TorneoId) throws Exception;
    // Métodos CRUD de Administración
    Competencia create(Long tournamentId, Competencia competencia) throws Exception;
    Competencia update(Long id, Competencia competencia) throws Exception;
    void delete(Long id) throws Exception;
    Competencia findById(Long id);
    /** Retorna la cantidad total de inscripciones para una competencia. */
    long countInscripciones(Long competenciaId);
    
    /** Retorna el monto total recaudado por las inscripciones en una competencia. */
    BigDecimal sumRecaudacion(Long competenciaId);
    
    /** Retorna el listado de todas las inscripciones para una competencia (Admin view). */
    List<Inscripcion> getInscripciones(Long competenciaId);
}
