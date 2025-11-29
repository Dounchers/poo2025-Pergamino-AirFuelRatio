package ar.edu.unnoba.poo2025.torneos.resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.service.InscriptionServiceImp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.edu.unnoba.poo2025.torneos.dto.CompetitionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.dto.TournamentResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.service.AuthorizationService;
import ar.edu.unnoba.poo2025.torneos.service.CompetitionService;
import ar.edu.unnoba.poo2025.torneos.service.TournamentService;
import ar.edu.unnoba.poo2025.torneos.service.InscriptionService;
import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;
import ar.edu.unnoba.poo2025.torneos.dto.InscripcionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.exception.*;

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

    @Autowired
    private InscriptionService inscriptionService;

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
    public ResponseEntity<?> getTournamentById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization){
        
        if (authorization == null || authorization.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        try {
            authorizationService.authorize(authorization);

            Torneo tournament = tournamentService.findById(id);
            if (tournament == null) {
                return ResponseEntity.notFound().build();
            }
            TournamentResponseDTO responseDTO = modelMapper.map(tournament, TournamentResponseDTO.class);
            return ResponseEntity.ok(responseDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Torneo no encontrado"));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }
    }

    @GetMapping("/{tournamentId}/competitions")
    public ResponseEntity<?> getCompetitionsByTournament(
            @PathVariable Long tournamentId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        if (authorization == null || authorization.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        try {
            authorizationService.authorize(authorization);

            List<CompetitionResponseDTO> competitions = competitionService.findByTournamentId(tournamentId);
            if (competitions.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(competitions);
        } catch (TournamentNotPublishedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Torneo no publicado"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Torneo no encontrado"));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //Retornar todos los datos de una competencia de un torneo (publicado)
    @GetMapping("/{tournamentId}/competition/{competitionId}")
    public ResponseEntity<?> getCompetitionById(@PathVariable Long tournamentId,
                                                @PathVariable Long competitionId,
                                                @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || authorization.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        try {
            authorizationService.authorize(authorization);

            CompetitionResponseDTO competition = competitionService.findByIdAndTorneoId(competitionId, tournamentId);
            if (competition == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(competition);
        }catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Competencia no pertenece al torneo o torneo no publicado"));

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }
    }

    @PostMapping("/{tournamentId}/competition/{competitionId}/inscriptions")
    public ResponseEntity<?> inscribeParticipant(
            @PathVariable Long tournamentId,
            @PathVariable Long competitionId,
            @RequestHeader(value = "Authorization", required = false) String authorization){

            if (authorization == null || authorization.isEmpty()) {
                return ResponseEntity.status(401).build();
            }
            try {
                Participante participant = authorizationService.authorize(authorization);

                Inscripcion nuevaInscripcion = inscriptionService.inscribeParticipant(tournamentId, competitionId, participant.getId());

                InscripcionResponseDTO responseDTO = modelMapper.map(nuevaInscripcion, InscripcionResponseDTO.class);

                return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

            } catch (InvalidTokenException | UserNotFoundException e) {
                // Excepciones lanzadas por el servicio de autenticación
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido o expirado."));
            } catch (AlreadyInscribedException e) {
                // El participante ya está inscrito en esa competencia
                return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict
                        .body(Map.of("error", e.getMessage()));
            } catch (NoCapacityException | TournamentNotPublishedException | EnrollmentDateExceededException e) {
                // Reglas de negocio (no hay cupo, torneo no abierto, etc.)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 Bad Request
                        .body(Map.of("error", e.getMessage()));
            } catch (Exception e) {
                // Manejo de cualquier otro error no esperado
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                        .body(Map.of("error", "Error interno del servidor."));
            }
        }
    }

