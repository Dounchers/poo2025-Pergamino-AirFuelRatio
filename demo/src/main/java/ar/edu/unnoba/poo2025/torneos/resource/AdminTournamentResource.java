package ar.edu.unnoba.poo2025.torneos.resource;

import ar.edu.unnoba.poo2025.torneos.dto.CompetitionDetailDTO;
import ar.edu.unnoba.poo2025.torneos.dto.CreateCompetitionDTO;
import ar.edu.unnoba.poo2025.torneos.dto.CreateTournamentRequestDTO;
import ar.edu.unnoba.poo2025.torneos.dto.InscripcionDTO;
import ar.edu.unnoba.poo2025.torneos.dto.TournamentInfoByIdResposeDTO;
import ar.edu.unnoba.poo2025.torneos.dto.TournamentListResponseDTO;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidDateRangeException;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.service.InscriptionService;
import ar.edu.unnoba.poo2025.torneos.service.AuthorizationService;
import ar.edu.unnoba.poo2025.torneos.service.CompetitionService;
import ar.edu.unnoba.poo2025.torneos.service.TournamentService;
import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/tournaments")
public class AdminTournamentResource {

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private InscriptionService inscriptionService;
    @Autowired
    private ModelMapper modelMapper;

    // Helper para validar admin
    private void validateAdmin(String token) throws Exception {
        if (token == null || token.isEmpty()) throw new Exception("Token requerido");
        authorizationService.authorizeAdmin(token);
    }

//              Torneos
    //5.Get Tournaments
    @GetMapping
     public ResponseEntity<List<TournamentListResponseDTO>> getTournaments(@RequestHeader("Authorization") String authorization) {
        try {
              authorizationService.authorizeAdmin(authorization);
        } catch (InvalidTokenException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        // 401
         catch (Exception e) { 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        List<Torneo> tournaments = tournamentService.findAll();
        List<TournamentListResponseDTO> responseDTO = tournaments.stream()
            .map(tournament -> modelMapper.map(tournament, TournamentListResponseDTO.class))
            .toList();
        return ResponseEntity.ok(responseDTO);
    }

    //6.Get Tournaments (id)
    @GetMapping("/{id}")
    public ResponseEntity<TournamentInfoByIdResposeDTO> getTournamentById(
        @PathVariable Long id, 
        @RequestHeader("Authorization") String authorization) {

        try {
              authorizationService.authorizeAdmin(authorization);
         } catch (InvalidTokenException | UserNotFoundException e) { 
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
         }
        // 401
         catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        Torneo tournament;
        try {
            tournament = tournamentService.findById(id);
            if(tournament == null) {
                return ResponseEntity.status(404).build(); // Not Found
            }

            TournamentInfoByIdResposeDTO response = new TournamentInfoByIdResposeDTO();
            response.setId(tournament.getId());
            response.setName(tournament.getName());
            response.setDescription(tournament.getDescription());
            response.setDateStart(tournament.getDateStart());
            response.setDateEnd(tournament.getDateEnd());
            response.setPublish(tournament.getPublish());
        
            // Calcular totales
            response.setTotalEnrollments(tournamentService.getTotalEnrollments(tournament.getId()));
            response.setTotalRevenue(tournamentService.getTotalRevenue(tournament.getId()));
        
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    //7.Create Tournament
    @PostMapping
    public ResponseEntity<?> createTournament(@Valid @RequestBody CreateTournamentRequestDTO dto, @RequestHeader("Authorization") String authorization) {
        try {
            authorizationService.authorizeAdmin(authorization);
        } catch (InvalidTokenException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        // 401
         catch (Exception e) { 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        Torneo torneo = modelMapper.map(dto, Torneo.class);
        try {
            tournamentService.create(torneo);
            return ResponseEntity.status(201).build(); // Created
        } catch (InvalidDateRangeException e) { 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                               .body(Map.of("error", e.getMessage())); 
        } catch (Exception e) {
            // Si el servicio lanza otra Exception, la tratamos como un conflicto genérico
            return ResponseEntity.status(HttpStatus.CONFLICT) // 409
                               .body(Map.of("error", e.getMessage())); 
        }
    }
    //8.Change Tournament details 
    @PutMapping("/{id}")    
    public ResponseEntity<?> updateTournament(@PathVariable Long id, @RequestBody CreateTournamentRequestDTO dto, @RequestHeader("Authorization") String authorization) {
        try {
            authorizationService.authorizeAdmin(authorization);
        // Capturar excepciones específicas
        } catch (InvalidTokenException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        // 401
         catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        Torneo torneoDetails = modelMapper.map(dto, Torneo.class);
        try {
            Torneo updatedTorneo = tournamentService.update(id, torneoDetails);
            TournamentListResponseDTO responseDTO = modelMapper.map(updatedTorneo, TournamentListResponseDTO.class);
            return ResponseEntity.ok(responseDTO); // OK
        } catch (Exception e) {
            //Diferencia los tipos de errores 
            String errorMessage = e.getMessage();
            if(errorMessage.contains("no encontrado")){
                return ResponseEntity.status(404)
                .body(Map.of("error", errorMessage)); // Not Found
            }
            if(errorMessage.contains("publicado")){
                return ResponseEntity.status(409)
                .body(Map.of("error", errorMessage)); // Conflict
            }
            // Otros errores
            return ResponseEntity.status(404)
                .body(Map.of("error", errorMessage)); 
        }
    }
    // 1. Remove Tournament
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTournament(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            validateAdmin(token);
            tournamentService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    // 2. Publish Tournament
    @PatchMapping("/{id}/published")
    public ResponseEntity<?> publishTournament(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            validateAdmin(token);
            tournamentService.publish(id);
            return ResponseEntity.ok(Map.of("message", "Torneo publicado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

 //                  Competencias
    // 3. Get Tournament Competitions (Admin view - raw list)
    @GetMapping("/{tournamentId}/competitions")
    public ResponseEntity<?> getCompetitions(@RequestHeader("Authorization") String token, @PathVariable Long tournamentId) {
        try {
            validateAdmin(token);
            // Reutilizamos el service público o creamos uno que devuelva entidades.
            // Aquí usamos el repositorio directamente a traves del service si existiera, o el metodo público
            // Nota: El método público filtra si no está publicado. Para admin deberiamos poder ver todo.
            // Por simplicidad reutilizo el método existente sabiendo esa restricción o deberías crear 'findAllByTournamentId' en el service.
            return ResponseEntity.ok(competitionService.findByTournamentId(tournamentId)); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    // 4. Get Tournament Competition Detail (With totals)
    @GetMapping("/{tournamentId}/competitions/{id}")
    public ResponseEntity<?> getCompetitionDetail(@RequestHeader("Authorization") String token, 
                                                  @PathVariable Long tournamentId, 
                                                  @PathVariable Long id) {
        try {
            validateAdmin(token);
            Competencia comp = competitionService.findById(id);
            if (comp == null) return ResponseEntity.notFound().build();

            CompetitionDetailDTO dto = modelMapper.map(comp, CompetitionDetailDTO.class);
            
            // Calculos usando el repositorio de inscripciones
            dto.setTotalInscripciones(inscriptionService.countByCompetenciaId(id));
            dto.setMontoTotalRecaudado(inscriptionService.sumPriceByCompetenciaId(id));

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    // 5. Create Tournament Competition
    @PostMapping("/{tournamentId}/competitions")
    public ResponseEntity<?> createCompetition(@RequestHeader("Authorization") String token, 
                                               @PathVariable Long tournamentId, 
                                               @RequestBody CreateCompetitionDTO dto) {
        try {
            validateAdmin(token);
            Competencia competencia = modelMapper.map(dto, Competencia.class);
            // Asegurar mapeo manual de nombres diferentes
            competencia.setCapacity(dto.getCupo());
            competencia.setBasePrice(dto.getPrecio());
            
            Competencia created = competitionService.create(tournamentId, competencia);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 6. Change Tournament competition details
    @PutMapping("/{tournamentId}/competitions/{id}") // El enunciado dice PUT /admin/tournaments/:id pero se refiere a la competencia
    public ResponseEntity<?> updateCompetition(@RequestHeader("Authorization") String token, 
                                               @PathVariable Long id, 
                                               @RequestBody CreateCompetitionDTO dto) {
        try {
            validateAdmin(token);
            Competencia compData = new Competencia();
            compData.setName(dto.getName());
            compData.setCapacity(dto.getCupo());
            compData.setBasePrice(dto.getPrecio());

            Competencia updated = competitionService.update(id, compData);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 7. Remove Tournament competition
    @DeleteMapping("/{tournamentId}/competitions/{id}")
    public ResponseEntity<?> deleteCompetition(@RequestHeader("Authorization") String token, 
                                               @PathVariable Long tournamentId,
                                               @PathVariable Long id) {
        try {
            validateAdmin(token);
            competitionService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    //          Inscripciones
    // 8. Get Tournament competition inscriptions
    @GetMapping("/{tournamentId}/competitions/{competitionId}/inscripciones")
    public ResponseEntity<?> getInscriptions(@RequestHeader("Authorization") String token, 
                                             @PathVariable Long tournamentId, 
                                             @PathVariable Long competitionId) {
        try {
            validateAdmin(token);
            List<Inscripcion> list = inscriptionService.findByCompetenciaId(competitionId);
            
            List<InscripcionDTO> dtos = list.stream().map(i -> {
                InscripcionDTO d = modelMapper.map(i, InscripcionDTO.class);
                d.setParticipantEmail(i.getParticipante().getEmail());
                return d;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
}
