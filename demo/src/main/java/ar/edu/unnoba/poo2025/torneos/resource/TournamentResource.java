package ar.edu.unnoba.poo2025.torneos.resource;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unnoba.poo2025.torneos.dto.TournamentResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.service.AuthorizationService;
import ar.edu.unnoba.poo2025.torneos.service.TournamentService;

@RestController
@RequestMapping("/tournaments")
public class TournamentResource {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    TournamentService tournamentService;
        

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
    
}
