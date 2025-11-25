package ar.edu.unnoba.poo2025.torneos.service;

import java.util.List;

import ar.edu.unnoba.poo2025.torneos.dto.CompetitionResponseDTO;

public interface CompetitionService {
  List<CompetitionResponseDTO> findByTournamentId(Long tournamentId);
}
