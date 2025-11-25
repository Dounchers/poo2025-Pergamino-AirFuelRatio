package ar.edu.unnoba.poo2025.torneos.resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unnoba.poo2025.torneos.dto.CompetitionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.dto.TournamentResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.service.AuthorizationService;
import ar.edu.unnoba.poo2025.torneos.service.CompetitionService;
import ar.edu.unnoba.poo2025.torneos.service.TournamentService;

@RestController
@RequestMapping("/tournaments")
public class TournamentResource {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    TournamentService tournamentService;
        
    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ModelMapper modelMapper;


    @GetMapping
    public ResponseEntity<List<TournamentResponseDTO>> getTournaments(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        // Validar que el header Authorization existe
        if (authorization == null || authorization.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            authorizationService.authorize(authorization);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
        
        List<Torneo> tournaments = tournamentService.getPublishedTournaments();
        List<TournamentResponseDTO> responseDTO = tournaments.stream()
            .map(torneo -> modelMapper.map(torneo, TournamentResponseDTO.class))
            .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTO);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTournamentById(@PathVariable Long id){
        try {
            Torneo tournament = tournamentService.findById(id);
            if(tournament == null){
                return ResponseEntity.notFound().build();
            }
            TournamentResponseDTO responseDTO = modelMapper.map(tournament, TournamentResponseDTO.class);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{tournamentId}/competitions")
    public ResponseEntity<?> getCompetitionsByTournament(@PathVariable Long tournamentId) {
        try{
            List<CompetitionResponseDTO> competitions = competitionService.findByTournamentId(tournamentId);
            if(competitions.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(competitions);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
