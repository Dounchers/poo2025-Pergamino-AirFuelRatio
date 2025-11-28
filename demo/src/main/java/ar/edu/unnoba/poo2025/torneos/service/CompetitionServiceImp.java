package ar.edu.unnoba.poo2025.torneos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.unnoba.poo2025.torneos.dto.CompetitionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.repository.CompetitionRepository;

@Service
public class CompetitionServiceImp implements CompetitionService{
  
  @Autowired
  private CompetitionRepository competitionRepository;

  @Autowired
  private TournamentService tournamentService;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public List<CompetitionResponseDTO> findByTournamentId(Long tournamentId) throws Exception{

    Torneo tournament = tournamentService.findById(tournamentId);

    if(tournament == null){
      throw new Exception("Torneo no encontrado.");
    }

    if(!tournament.getPublish()){
      throw new Exception("Torneo no publicado.");
    }

    List<Competencia> competitions = competitionRepository.findByTournamentId(tournamentId);

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
  public Competencia create(Long tournamentId, Competencia competencia) throws Exception {
      Torneo torneo = tournamentService.findById(tournamentId);
      if (torneo == null) throw new Exception("Torneo no encontrado");
      if (torneo.getPublish()) throw new Exception("No se pueden agregar competencias a un torneo publicado");
      
      competencia.setTorneo(torneo);
      return competitionRepository.save(competencia);
  }

  @Override
  public Competencia update(Long id, Competencia datosNuevos) throws Exception {
      Competencia competencia = competitionRepository.findById(id).orElse(null);
      if (competencia == null) throw new Exception("Competencia no encontrada");
      if (competencia.getTorneo().getPublish()) throw new Exception("No se puede editar una competencia de un torneo publicado");

      competencia.setName(datosNuevos.getName());
      competencia.setCapacity(datosNuevos.getCapacity());
      competencia.setBasePrice(datosNuevos.getBasePrice());
      
      return competitionRepository.save(competencia);
  }

  @Override
  public void delete(Long id) throws Exception {
      Competencia competencia = competitionRepository.findById(id).orElse(null);
      if (competencia == null) throw new Exception("Competencia no encontrada");
      if (competencia.getTorneo().getPublish()) throw new Exception("No se puede eliminar una competencia de un torneo publicado");
      
      competitionRepository.delete(competencia);
  }

}
