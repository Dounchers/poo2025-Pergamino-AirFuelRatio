package ar.edu.unnoba.poo2025.torneos.service;

import java.util.List;
import ar.edu.unnoba.poo2025.torneos.dto.CompetitionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;

public interface CompetitionService {
    // Método de tu compañero (Listado público)
    List<CompetitionResponseDTO> findByTournamentId(Long tournamentId) throws Exception;

    // Tu método (Consulta detalle de competencia)
    CompetitionResponseDTO findByIdAndTorneoId(Long competitionId, Long TorneoId) throws Exception;

    // Métodos de tu compañero (Administración)
    Competencia create(Long tournamentId, Competencia competencia) throws Exception;
    Competencia update(Long id, Competencia competencia) throws Exception;
    void delete(Long id) throws Exception;
    Competencia findById(Long id);
}
