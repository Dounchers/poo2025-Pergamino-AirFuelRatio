package ar.edu.unnoba.poo2025.torneos.resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ar.edu.unnoba.poo2025.torneos.exception.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unnoba.poo2025.torneos.dto.CompetitionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.dto.InscripcionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.dto.TournamentResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.service.AuthorizationService;
import ar.edu.unnoba.poo2025.torneos.service.CompetitionService;
import ar.edu.unnoba.poo2025.torneos.service.InscriptionService;
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

    @Autowired
    private InscriptionService inscriptionService;

    @GetMapping
    public ResponseEntity<List<TournamentResponseDTO>> getTournaments(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        // Validar que el header Authorization existe
        if (authorization == null || authorization.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            authorizationService.authorize(authorization);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Torneo> tournaments = tournamentService.getPublishedTournaments();
        List<TournamentResponseDTO> responseDTO = tournaments.stream()
            .map(torneo -> modelMapper.map(torneo, TournamentResponseDTO.class))
            .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTO);
    }
    //retorna todos los datos de un torneo especfico (sólo validamos el caso que no exista)
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization){
        
        if (authorization == null || authorization.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
    //retorna todas las competencias de un torneo (publicado)
    @GetMapping("/{tournamentId}/competitions")
    public ResponseEntity<?> getCompetitionsByTournament(
            @PathVariable Long tournamentId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        if (authorization == null || authorization.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            authorizationService.authorize(authorization);

            List<CompetitionResponseDTO> competitions = competitionService.findByTournamentId(tournamentId);
            if (competitions.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            List<CompetitionResponseDTO> responseDTOs = competitions.stream()
                .map(comp -> modelMapper.map(comp, CompetitionResponseDTO.class))
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(responseDTOs);
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
        }catch (TournamentNotFoundException | CompetitionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (TournamentNotPublishedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Torneo no publicado"));

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
                // 401 - Problemas de identidad
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido o expirado."));

            } catch (TournamentNotFoundException | CompetitionNotFoundException e) {
                // 404 - No existe el recurso (Torneo o Competencia)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", e.getMessage()));

            } catch (TournamentNotPublishedException e) {
                // 403 - El torneo existe pero no se puede acceder porque no está publicado
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", e.getMessage()));

            } catch (AlreadyInscribedException e) {
                // 409 - Conflicto (Ya está en la base de datos)
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", e.getMessage()));

            } catch (NoCapacityException | EnrollmentDateExceededException e) {
                // 400 - Error del cliente (No hay cupo o se pasó de fecha)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", e.getMessage()));

            } catch (Exception e) {
                // 500 - Error inesperado (para que Bruno no reciba un error vacío)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Ocurrió un error inesperado: " + e.getMessage()));
            }
        }
    }

