package ar.edu.unnoba.poo2025.torneos.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import ar.edu.unnoba.poo2025.torneos.exception.CompetitionNotFoundException;
import ar.edu.unnoba.poo2025.torneos.exception.TournamentAlreadyPublishedException;
import ar.edu.unnoba.poo2025.torneos.exception.TournamentNotFoundException;
import ar.edu.unnoba.poo2025.torneos.exception.TournamentNotPublishedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.unnoba.poo2025.torneos.dto.CompetitionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.repository.CompetitionRepository;
import ar.edu.unnoba.poo2025.torneos.repository.InscriptionRepository;

@Service
public class CompetitionServiceImp implements CompetitionService{
  
  @Autowired
  private CompetitionRepository competitionRepository;

  @Autowired
  private TournamentService tournamentService;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
    private InscriptionRepository inscriptionRepository;

  @Override
  public List<CompetitionResponseDTO> findByTournamentId(Long tournamentId) {

    Torneo tournament = tournamentService.findById(tournamentId);

    if(tournament == null){
      throw new TournamentNotFoundException("Torneo no encontrado.");
    }

    if(!tournament.getPublish()){
      throw new TournamentNotPublishedException("Torneo no publicado.");
    }

    List<Competencia> competitions = competitionRepository.findByTorneoId(tournamentId);

    return competitions.stream()
            .map(comp -> {
              CompetitionResponseDTO dto = modelMapper.map(comp, CompetitionResponseDTO.class);
              dto.setTournamentName(comp.getTorneo().getName());
              return dto;
            })
            .collect(Collectors.toList());
  }
  @Override
  public Competencia findById(Long id) {
      return competitionRepository.findById(id).orElse(null);
  }

  @Override
  public Competencia create(Long tournamentId, Competencia competencia){
      Torneo torneo = tournamentService.findById(tournamentId);
      if (torneo == null) throw new TournamentNotFoundException("Torneo no encontrado");
      if (torneo.getPublish()) throw new TournamentAlreadyPublishedException("No se pueden agregar competencias a un torneo publicado");
      
      competencia.setTorneo(torneo);
      return competitionRepository.save(competencia);
  }

  @Override
  public Competencia update(Long id, Competencia datosNuevos) {
      Competencia competencia = competitionRepository.findById(id).orElse(null);
      if (competencia == null) throw new CompetitionNotFoundException("Competencia no encontrada");
      if (competencia.getTorneo().getPublish()) throw new TournamentAlreadyPublishedException("No se puede editar una competencia de un torneo publicado");

      competencia.setName(datosNuevos.getName());
      competencia.setCapacity(datosNuevos.getCapacity());
      competencia.setBasePrice(datosNuevos.getBasePrice());
      
      return competitionRepository.save(competencia);
  }

  @Override
  public void delete(Long id){
      Competencia competencia = competitionRepository.findById(id).orElse(null);
      if (competencia == null) throw new CompetitionNotFoundException("Competencia no encontrada");
      if (competencia.getTorneo().getPublish()) throw new TournamentAlreadyPublishedException("No se puede eliminar una competencia de un torneo publicado");
      
      competitionRepository.delete(competencia);
  }

  @Override
    public CompetitionResponseDTO findByIdAndTorneoId(Long competitionId, Long tournamentId) {
      Torneo tournament = tournamentService.findById(tournamentId);

      if(tournament == null){
        throw new TournamentNotPublishedException("Torneo no encontrado.");
      }

      if(!tournament.getPublish()){
        throw new TournamentNotPublishedException("Torneo no publicado.");
      }

      Competencia competition = competitionRepository.findByIdAndTorneoId(competitionId, tournamentId);

      if(competition == null){
        throw new CompetitionNotFoundException("Competencia no encontrada en el torneo especificado.");
      }

      CompetitionResponseDTO responseDTO = modelMapper.map(competition, CompetitionResponseDTO.class);

      responseDTO.setTournamentName(competition.getTorneo().getName());
      return responseDTO;
  }
  @Override
  public long countInscripciones(Long competenciaId) {
        return inscriptionRepository.countByCompetenciaId(competenciaId);
    }
  @Override
  public BigDecimal sumRecaudacion(Long competenciaId) {
        return inscriptionRepository.sumPriceByCompetenciaId(competenciaId);
    }
  @Override
  public List<Inscripcion> getInscripciones(Long competenciaId) {
        return inscriptionRepository.findByCompetenciaId(competenciaId);
    }
}
