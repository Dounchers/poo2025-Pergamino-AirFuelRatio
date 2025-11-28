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

}
