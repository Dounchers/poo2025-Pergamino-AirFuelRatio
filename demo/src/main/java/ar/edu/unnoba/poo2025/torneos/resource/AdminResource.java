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
import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;
import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import ar.edu.unnoba.poo2025.torneos.service.AdminService;
import ar.edu.unnoba.poo2025.torneos.service.AuthenticationService;
import ar.edu.unnoba.poo2025.torneos.service.AuthorizationService;


@RestController
@RequestMapping("/admin")
public class AdminResource {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private AdminService adminService;


    //1.Authentication
    @PostMapping(path = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
 	public ResponseEntity<?> authentication(@RequestBody AuthenticationRequestDTO authRequest) {
 		try {
 			Administrador administrador = modelMapper.map(authRequest, Administrador.class);

 			String jwtToken = authenticationService.authenticate(administrador);

 			Map<String, String> tokenResponse = Collections.singletonMap("token", jwtToken);
 			return ResponseEntity.ok(tokenResponse);

 		} catch (Exception e) {
 			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
 					.body(Map.of("error", e.getMessage()));
 		}
 	}

    //2.Get admin accounts - lista todos los administradores
    @GetMapping("/accounts")
    public ResponseEntity<List<AdminResponseDTO>> getAccounts(@RequestHeader("Authorization") String authorization) {
        try {
            authorizationService.authorizeAdmin(authorization);
        } catch (InvalidTokenException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        // El token es válido, pero el usuario no existe. Se interpreta como fallo de autenticación 401
         catch (Exception e) {
            // Se usa el 403 para cualquier otro problema de autorización/permiso no cubierto
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
        }

        List<Administrador> administradores = adminService.findAll();

         List<AdminResponseDTO> responseDTO = administradores.stream()
        .map(admin -> modelMapper.map(admin, AdminResponseDTO.class))
        .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTO);
    }

    //3.Create admin account
    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@RequestBody CreateAdminRequestDTO dto, @RequestHeader("Authorization") String authorization) {
        try {
            authorizationService.authorizeAdmin(authorization);
        } catch (InvalidTokenException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        // 401
         catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
       
        Administrador administrador = modelMapper.map(dto, Administrador.class);
       
        try {
            adminService.create(administrador);
            return ResponseEntity.status(201).build(); // Created
        } catch (DuplicateResourceException e) {
            // Manejo de error específico para recurso duplicado
            return ResponseEntity.status(HttpStatus.CONFLICT) // 409
                               .body(Map.of("error", e.getMessage())); 
        } catch (Exception e) {
            // Manejo de errores genéricos 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                               .body(Map.of("error", "Error interno al crear administrador"));
        }
    }
    
    //4.Delete admin account
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id, @RequestHeader("Authorization") String authorization) {
       //Validad que quien hace la request sea admin
    Administrador adminActual;
       try{
              adminActual = authorizationService.authorizeAdmin(authorization);
         // Capturar excepciones específicas
         } catch (InvalidTokenException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
         }
        // 401
         catch (Exception e) {
              return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
       }
         //Buscar el admin que se quiere eliminar (id)
        Administrador adminAEliminar;
        try{
            adminAEliminar = adminService.findById(id);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
        //Validar que el admin a eliminar no sea él mismo
        if(adminActual.getId().equals(adminAEliminar.getId())){
            return ResponseEntity.status(403).body(Map.of("error", "No puedes eliminar tu propia cuenta de administrador."));
        }
        //Eliminar el admin
         try {
            adminService.delete(id);
            return ResponseEntity.status(204).build(); // No Content
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Error al eliminar administrador"));
        }
    }
}