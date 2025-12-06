package ar.edu.unnoba.poo2025.torneos.resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

import ar.edu.unnoba.poo2025.torneos.exception.*;
import ar.edu.unnoba.poo2025.torneos.util.AdminValidator;
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
    @Autowired
    private AdminValidator adminValidator;

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
    @PostMapping("/{tournamentId}/competitions")
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
    @PutMapping("/{tournamentId}/competitions/{id}") // El enunciado dice PUT /admin/tournaments/:id pero se refiere a la competencia
    public ResponseEntity<?> updateCompetition(@RequestHeader("Authorization") String token, 
                                               @PathVariable Long id, 
                                               @RequestBody CreateCompetitionDTO dto) {

        validateAdmin(token);
        adminValidator.validate(token);
        try {
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
        adminValidator.validate(token);
        try {
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
