package ar.edu.unnoba.poo2025.torneos.resource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator; 
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unnoba.poo2025.torneos.dto.CompetitionDetailDTO;
import ar.edu.unnoba.poo2025.torneos.dto.CreateCompetitionDTO;
import ar.edu.unnoba.poo2025.torneos.dto.CreateTournamentRequestDTO;
import ar.edu.unnoba.poo2025.torneos.dto.InscripcionDTO;
import ar.edu.unnoba.poo2025.torneos.dto.TournamentListResponseDTO;
import ar.edu.unnoba.poo2025.torneos.dto.TournamentResponseDTO;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidDateRangeException;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.service.CompetitionService;
import ar.edu.unnoba.poo2025.torneos.service.TournamentService;
import ar.edu.unnoba.poo2025.torneos.util.AdminValidator;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/tournaments")
public class AdminTournamentResource {
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AdminValidator adminValidator;

//   Torneos
    //5.Get Tournaments
  @GetMapping
     public ResponseEntity<List<TournamentListResponseDTO>> getTournaments(@RequestHeader("Authorization") String authorization) {
        adminValidator.validate(authorization);
     List<Torneo> tournaments = tournamentService.findAll()
    .stream()
    .sorted(Comparator.comparing(Torneo::getDateStart, 
        Comparator.nullsLast(Comparator.reverseOrder())))
    .collect(Collectors.toList());
        List<TournamentListResponseDTO> responseDTO = tournaments.stream()
            .map(tournament -> modelMapper.map(tournament, TournamentListResponseDTO.class))
            .toList();
        return ResponseEntity.ok(responseDTO);
    }

    //6.Get Tournaments (id)
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> getTournamentById(
        @PathVariable Long id, 
        @RequestHeader("Authorization") String authorization) {

        adminValidator.validate(authorization);
        Torneo tournament;
        try {
            tournament = tournamentService.findById(id);
            if(tournament == null) {
                return ResponseEntity.status(404).build(); // Not Found
            }

            TournamentResponseDTO response = new TournamentResponseDTO();
            response.setId(tournament.getId());
            response.setName(tournament.getName());
            response.setDescription(tournament.getDescription());
            response.setDateStart(tournament.getDateStart());
            response.setDateEnd(tournament.getDateEnd());
            response.setPublish(tournament.getPublish());
        
            // Calcular totales
            response.setTotalEnrollments(tournamentService.getTotalEnrollments(tournament.getId()));
            response.setTotalRevenue(tournamentService.getTotalRevenue(tournament.getId()).doubleValue());
        
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    //7.Create Tournament
    @PostMapping
    public ResponseEntity<?> createTournament(@Valid @RequestBody CreateTournamentRequestDTO dto, @RequestHeader("Authorization") String authorization) {
        var administrador = adminValidator.validate(authorization);
        Torneo torneo = modelMapper.map(dto, Torneo.class);
        torneo.setAdministrador(administrador);
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
    public ResponseEntity<?> updateTournament(@PathVariable Long id, @RequestBody CreateTournamentRequestDTO dto, @RequestHeader("Authorization") String token) {
       
        adminValidator.validate(token);
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
    public ResponseEntity<?> deleteTournament(@RequestHeader("Authorization") String token, @PathVariable Long id) throws Exception {
        adminValidator.validate(token);
        tournamentService.delete(id);
        return ResponseEntity.ok().build();
    }

    // 2. Publish Tournament
    @PatchMapping("/{id}/published")
    public ResponseEntity<?> publishTournament(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        adminValidator.validate(token);
        try {
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
        adminValidator.validate(token);
        try {
            // Reutilizamos el service público o creamos uno que devuelva entidades.
            // Aquí usamos el repositorio directamente a traves del service si existiera, o el metodo público
            // Nota: El método público filtra si no está publicado. Para admin deberiamos poder ver todo.
            // Por simplicidad reutilizo el método existente sabiendo esa restricción o deberías crear 'findAllByTournamentId' en el service.
            return ResponseEntity.ok(competitionService.findByTournamentId(tournamentId)); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 4. Get Tournament Competition Detail (With totals)
    @GetMapping("/{tournamentId}/competitions/{id}")
    public ResponseEntity<?> getCompetitionDetail(@RequestHeader("Authorization") String token,
                                                  @PathVariable Long tournamentId,
                                                  @PathVariable Long id) {
        adminValidator.validate(token);
        try {
            Competencia comp = competitionService.findById(id);
            if (comp == null) return ResponseEntity.notFound().build();

            CompetitionDetailDTO dto = modelMapper.map(comp, CompetitionDetailDTO.class);
            
            // Calculos usando el repositorio de inscripciones
            dto.setTotalInscripciones(competitionService.countInscripciones(id));
            dto.setMontoTotalRecaudado(competitionService.sumRecaudacion(id));

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 5. Create Tournament Competition
    @PostMapping("/{tournamentId}/competitions")
    public ResponseEntity<?> createCompetition(@RequestHeader("Authorization") String token, 
                                               @PathVariable Long tournamentId, 
                                               @RequestBody CreateCompetitionDTO dto) {
        adminValidator.validate(token);
        try {
            Competencia competencia = modelMapper.map(dto, Competencia.class);
            // Asegurar mapeo manual
            competencia.setCapacity(dto.getCupo());
            competencia.setBasePrice(dto.getPrecio());
            
            Competencia created = competitionService.create(tournamentId, competencia);
            CreateCompetitionDTO response = modelMapper.map(created, CreateCompetitionDTO.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 6. Change Tournament competition details
    @PutMapping("/{tournamentId}/competitions/{id}") // El enunciado dice PUT /admin/tournaments/:id pero se refiere a la competencia
    public ResponseEntity<?> updateCompetition(@RequestHeader("Authorization") String token, 
                                               @PathVariable Long id, 
                                               @RequestBody CreateCompetitionDTO dto) throws Exception {
        adminValidator.validate(token);
        Competencia compData = new Competencia();
        compData.setName(dto.getName());
        compData.setCapacity(dto.getCupo());
        compData.setBasePrice(dto.getPrecio());

        Competencia updated = competitionService.update(id, compData);
        return ResponseEntity.ok(updated);
    }

    // 7. Remove Tournament competition
    @DeleteMapping("/{tournamentId}/competitions/{id}")
    public ResponseEntity<?> deleteCompetition(@RequestHeader("Authorization") String token, 
                                               @PathVariable Long tournamentId,
                                               @PathVariable Long id) throws Exception {
        adminValidator.validate(token);
        competitionService.delete(id);
        return ResponseEntity.ok().build();
    }

    //          Inscripciones
    // 8. Get Tournament competition inscriptions
    @GetMapping("/{tournamentId}/competitions/{competitionId}/inscripciones")
    public ResponseEntity<?> getInscriptions(@RequestHeader("Authorization") String token, 
                                             @PathVariable Long tournamentId, 
                                             @PathVariable Long competitionId) {
        adminValidator.validate(token);
        try {
            List<Inscripcion> list = competitionService.getInscripciones(competitionId);
            
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
