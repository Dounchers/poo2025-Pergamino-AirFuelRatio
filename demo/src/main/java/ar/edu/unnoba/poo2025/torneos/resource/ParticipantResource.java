package ar.edu.unnoba.poo2025.torneos.resource;

import ar.edu.unnoba.poo2025.torneos.dto.CreateParticipantRequestDTO;
import ar.edu.unnoba.poo2025.torneos.dto.ParticipantResponseDTO;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.service.ParticipantService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/participants")
public class ParticipantResource {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ModelMapper modelMapper;

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
