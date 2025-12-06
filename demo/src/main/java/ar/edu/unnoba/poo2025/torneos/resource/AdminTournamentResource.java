package ar.edu.unnoba.poo2025.torneos.resource;

import ar.edu.unnoba.poo2025.torneos.dto.CompetitionDetailDTO;
import ar.edu.unnoba.poo2025.torneos.dto.CreateCompetitionDTO;
import ar.edu.unnoba.poo2025.torneos.dto.InscripcionDTO;
import ar.edu.unnoba.poo2025.torneos.exception.AuthorizationFailedException;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.TournamentAlreadyPublishedException;
import ar.edu.unnoba.poo2025.torneos.exception.TournamentNotFoundException;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;
import ar.edu.unnoba.poo2025.torneos.repository.InscriptionRepository;
import ar.edu.unnoba.poo2025.torneos.service.AdminAuthorizationService;
import ar.edu.unnoba.poo2025.torneos.service.CompetitionService;
import ar.edu.unnoba.poo2025.torneos.service.TournamentService;
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
    private AdminAuthorizationService adminAuthService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private ModelMapper modelMapper;

    // Helper para validar admin
    private void validateAdmin(String token) {
        if (token == null || token.isEmpty()) throw new InvalidTokenException("Token requerido");
        adminAuthService.authorize(token);
    }

    // 1. Remove Tournament
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTournament(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        validateAdmin(token);
        tournamentService.delete(id);
        return ResponseEntity.ok().build();
    }

    // 2. Publish Tournament
    @PatchMapping("/{id}/published")
    public ResponseEntity<?> publishTournament(@PathVariable Long id) {
        //validateAdmin(token);
        tournamentService.publish(id);
        return ResponseEntity.ok(Map.of("message", "Torneo publicado"));
    }

    // 3. Get Tournament Competitions (Admin view - raw list)
    @GetMapping("/{tournamentId}/competitions")
    public ResponseEntity<?> getCompetitions(@RequestHeader("Authorization") String token, @PathVariable Long tournamentId) {
        validateAdmin(token);
        // Reutilizamos el service público o creamos uno que devuelva entidades.
        // Aquí usamos el repositorio directamente a traves del service si existiera, o el metodo público
        // Nota: El método público filtra si no está publicado. Para admin deberiamos poder ver todo.
        // Por simplicidad reutilizo el método existente sabiendo esa restricción o deberías crear 'findAllByTournamentId' en el service.
        return ResponseEntity.ok(competitionService.findByTournamentId(tournamentId));
    }

    // 4. Get Tournament Competition Detail (With totals)
    @GetMapping("/{tournamentId}/competitions/{id}")
    public ResponseEntity<?> getCompetitionDetail(@RequestHeader("Authorization") String token,
                                                  @PathVariable Long tournamentId,
                                                  @PathVariable Long id) {
        validateAdmin(token);
        Competencia comp = competitionService.findById(id);
        CompetitionDetailDTO dto = modelMapper.map(comp, CompetitionDetailDTO.class);
        dto.setTotalInscripciones(competitionService.countInscripciones(id));
        dto.setMontoTotalRecaudado(competitionService.sumRecaudacion(id));
        return ResponseEntity.ok(dto);
    }

    // 5. Create Tournament Competition
    @PostMapping("/{tournamentId}")
    public ResponseEntity<?> createCompetition(@RequestHeader("Authorization") String token, 
                                               @PathVariable Long tournamentId, 
                                               @RequestBody CreateCompetitionDTO dto) {
        validateAdmin(token);
        Competencia competencia = modelMapper.map(dto, Competencia.class);
        competencia.setCapacity(dto.getCupo());
        competencia.setBasePrice(dto.getPrecio());
        Competencia created = competitionService.create(tournamentId, competencia);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 6. Change Tournament competition details
    @PutMapping("/{id}") // El enunciado dice PUT /admin/tournaments/:id pero se refiere a la competencia
    public ResponseEntity<?> updateCompetition(@RequestHeader("Authorization") String token, 
                                               @PathVariable Long id, 
                                               @RequestBody CreateCompetitionDTO dto) {

            validateAdmin(token);
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
                                               @PathVariable Long id) {
        try {
            validateAdmin(token);
            competitionService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 8. Get Tournament competition inscriptions
    @GetMapping("/{tournamentId}/competitions/{competitionId}/inscripciones")
    public ResponseEntity<?> getInscriptions(@RequestHeader("Authorization") String token,
                                             @PathVariable Long tournamentId, 
                                             @PathVariable Long competitionId) {
        try {
            validateAdmin(token);
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
