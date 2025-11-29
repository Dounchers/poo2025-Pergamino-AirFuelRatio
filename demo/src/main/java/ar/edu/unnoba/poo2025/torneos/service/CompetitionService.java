package ar.edu.unnoba.poo2025.torneos.service;

import java.util.List;

import ar.edu.unnoba.poo2025.torneos.dto.CompetitionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;

public interface CompetitionService {
  List<CompetitionResponseDTO> findByTournamentId(Long tournamentId) throws Exception;
  Competencia create(Long tournamentId, Competencia competencia) throws Exception;
  Competencia update(Long id, Competencia competencia) throws Exception;
  void delete(Long id) throws Exception;
  Competencia findById(Long id);
}

