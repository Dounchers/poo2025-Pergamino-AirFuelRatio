package ar.edu.unnoba.poo2025.torneos.resource;

import ar.edu.unnoba.poo2025.torneos.dto.InscriptionDetailDTO;
import ar.edu.unnoba.poo2025.torneos.service.InscriptionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ar.edu.unnoba.poo2025.torneos.dto.ParticipantInscriptionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;
import ar.edu.unnoba.poo2025.torneos.service.AuthorizationService;
import ar.edu.unnoba.poo2025.torneos.exception.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class InscriptionsResource {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private InscriptionService inscriptionService;

    //Lo accede el participante (ya logueado) para ver sus inscripciones.
    @GetMapping("/inscriptions")
    public ResponseEntity <?> getInscriptions(@RequestHeader (value = "Authorization", required = false) String authorization) {

        if (authorization == null || authorization.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        try {
            // Obtener el participante autorizado
            Participante participant = authorizationService.authorize(authorization);
            Long participantId = participant.getId();

            // Obtener las inscripciones del participante
            List<Inscripcion> inscriptions = inscriptionService.findInscriptionsByParticipant(participantId);

            if (inscriptions.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            // Mapear las inscripciones a DTOs de respuesta
            List<ParticipantInscriptionResponseDTO> responseDTOs = inscriptions.stream()
                    .map(ParticipantInscriptionResponseDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(responseDTOs);

        } catch (InvalidTokenException | UserNotFoundException e) {
            // Solo fallos de autenticación lanzados por authorizationService
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado."));
        } catch (Exception e) {
            // Otros errores (DB, lógica)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno."));
        }
    }

    @GetMapping("/inscriptions/{inscriptionId}")
    public ResponseEntity<?> getInscriptionDetailsById(
            @PathVariable("inscriptionId") Long inscriptionId,
            @RequestHeader (value = "Authorization", required = false) String authorization){

        if(authorization==null || authorization.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            // Obtener el participante autorizado
            Participante participant = authorizationService.authorize(authorization);
            Long participantId = participant.getId();

            // Obtener los detalles de la inscripción
            Inscripcion inscripcion = inscriptionService.findInscriptionDetailsByIdAndParticipantId(inscriptionId, participantId);

            // Mapear la inscripción a DTO de detalle
            InscriptionDetailDTO responseDTO = new InscriptionDetailDTO(inscripcion);

            return ResponseEntity.ok().body(responseDTO);

        } catch (InvalidTokenException | UserNotFoundException e) {
            // Excepciones de autenticación
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado."));
        } catch (ResourceNotFoundException e) {
            // Excepción lanzada por el servicio si no se encuentra la inscripción
            // o si el ID de la inscripción no corresponde al ID del participante (IDOR prevenido).
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Inscripción no encontrada."));
        } catch (Exception e) {
            // Manejo de cualquier otro error no esperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al obtener detalles."));
        }

    }
}

