package ar.edu.unnoba.poo2025.torneos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.unnoba.poo2025.torneos.model.Competencia;

public interface CompetitionRepository extends JpaRepository<Competencia, Long>{

  //Buscar todas las competencias asociadas a un torneo por su ID.
  List<Competencia> findByTournamentId(Long TournamentId);
}
