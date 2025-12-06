package ar.edu.unnoba.poo2025.torneos.resource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unnoba.poo2025.torneos.dto.AdminResponseDTO;
import ar.edu.unnoba.poo2025.torneos.dto.AuthenticationRequestDTO;
import ar.edu.unnoba.poo2025.torneos.dto.CreateAdminRequestDTO;
import ar.edu.unnoba.poo2025.torneos.exception.DuplicateResourceException;
import ar.edu.unnoba.poo2025.torneos.exception.ResourceNotFoundException;
import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import ar.edu.unnoba.poo2025.torneos.service.AdminService;
import ar.edu.unnoba.poo2025.torneos.service.AuthenticationService;
import ar.edu.unnoba.poo2025.torneos.util.AdminValidator;


@RestController
@RequestMapping("/admin")
public class AdminResource {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminValidator adminValidator;


    //1.Authentication
    @PostMapping(path = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
 	public ResponseEntity<?> authentication(@RequestBody AuthenticationRequestDTO authRequest) throws Exception {
 		Administrador administrador = modelMapper.map(authRequest, Administrador.class);
 		String jwtToken = authenticationService.authenticate(administrador);
 		Map<String, String> tokenResponse = Collections.singletonMap("token", jwtToken);
 		return ResponseEntity.ok(tokenResponse);
 	}

    //2.Get admin accounts - lista todos los administradores
    @GetMapping("/accounts")
    public ResponseEntity<List<AdminResponseDTO>> getAccounts(@RequestHeader("Authorization") String authorization) {
        adminValidator.validate(authorization);

        List<Administrador> administradores = adminService.findAll();
        List<AdminResponseDTO> responseDTO = administradores.stream()
            .map(admin -> modelMapper.map(admin, AdminResponseDTO.class))
            .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTO);
    }

    //3.Create admin account
    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@RequestBody CreateAdminRequestDTO dto, @RequestHeader("Authorization") String authorization) {
        adminValidator.validate(authorization);
       
        Administrador administrador = modelMapper.map(dto, Administrador.class);
        adminService.create(administrador);
        return ResponseEntity.status(201).build(); // Created
    }
    
    //4.Delete admin account
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id, @RequestHeader("Authorization") String authorization) throws Exception {
        Administrador adminActual = adminValidator.validate(authorization);
        
        Administrador adminAEliminar = adminService.findById(id);
        if (adminAEliminar == null) {
            throw new ResourceNotFoundException("Administrador no encontrado");
        }

        if(adminActual.getId().equals(adminAEliminar.getId())){
            return ResponseEntity.status(403).body(Map.of("error", "No puedes eliminar tu propia cuenta de administrador."));
        }

        adminService.delete(id);
        return ResponseEntity.status(204).build(); // No Content
    }
}