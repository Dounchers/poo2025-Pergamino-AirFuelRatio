package ar.edu.unnoba.poo2025.torneos.resource;

import java.util.Collections;
import ar.edu.unnoba.poo2025.torneos.exception.AuthenticationFailedException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unnoba.poo2025.torneos.dto.AuthenticationRequestDTO;
import ar.edu.unnoba.poo2025.torneos.dto.CreateParticipantRequestDTO;
import ar.edu.unnoba.poo2025.torneos.dto.ParticipantResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.service.AuthenticationService;
import ar.edu.unnoba.poo2025.torneos.service.ParticipantService;

@RestController
@RequestMapping("/participants")
public class ParticipantResource {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
 	private AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateParticipantRequestDTO requestDTO){
        try {
            //Mapea el DTO al model de participante
            Participante participant = modelMapper.map(requestDTO, Participante.class);

            //El endpoint delega la lógica de creación
            Participante participantCreated = participantService.create(participant);

            ParticipantResponseDTO responseDTO = modelMapper.map(participantCreated, ParticipantResponseDTO.class);

            //Si se creó retorna un 201 (nuevo recurso) junto con el body de los datos del dto
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error al crear el participante: "+ e.getMessage());
        }
    }
    // NUEVO MÉTODO DE AUTENTICACIÓN
 	@PostMapping(path = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
 	public ResponseEntity<?> authentication(@RequestBody AuthenticationRequestDTO authRequest) {
 		try {
 			// 1. Mapear DTO a la entidad Participante
 			Participante participant = modelMapper.map(authRequest, Participante.class);

 			// 2. Delegar la autenticación al servicio
 			String jwtToken = authenticationService.authenticate(participant);

 			// 3. Retornar 200 OK con el token en el body
 			// Usamos Map para crear el JSON: { "token": "Bearer ..." }
 			Map<String, String> tokenResponse = Collections.singletonMap("token", jwtToken);
 			return ResponseEntity.ok(tokenResponse);

        } catch (UserNotFoundException | AuthenticationFailedException e) {
            // Si el usuario no existe O la clave es incorrecta, devolvemos el mismo 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas: Email o password incorrecto."));

 		} catch (Exception e) {
 			// 4. Si falla (email no existe, contraseña no coincide), retornar 401 Unauthorized
 			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
 					.body(Map.of("error", e.getMessage()));
 		}
 	}

    @GetMapping
    public ResponseEntity<List<ParticipantResponseDTO>> getAll(){
        try {
            List<Participante> participants = participantService.findAll();
            if(participants.isEmpty()){
                return ResponseEntity.noContent().build();
            }

            List<ParticipantResponseDTO> responseDTOs = participants.stream()
                    .map(participante -> modelMapper.map(participante, ParticipantResponseDTO.class))
                    .toList();

            return ResponseEntity.ok(responseDTOs);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(params = "email")
    public ResponseEntity<ParticipantResponseDTO> getByEmail(@RequestParam("email") String email){
        try{
            Participante participante = participantService.findByEmail(email);
            if(participante == null){
                return ResponseEntity.notFound().build();
            }
            ParticipantResponseDTO participantResponseDTO = modelMapper.map(participante, ParticipantResponseDTO.class);
            return  ResponseEntity.ok(participantResponseDTO);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
