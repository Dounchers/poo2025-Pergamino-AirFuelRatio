package ar.edu.unnoba.poo2025.torneos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.unnoba.poo2025.torneos.exception.AuthenticationFailedException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;
import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.util.JwtTokenUtil;
import ar.edu.unnoba.poo2025.torneos.util.PasswordEncoder;

@Service
public class AuthenticationServiceImp implements AuthenticationService {

    // Usamos la interfaz del servicio, no la implementación
    @Autowired
    private  ParticipantService participantService;
    
    @Autowired
    private  AdminService adminService;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private  JwtTokenUtil jwtTokenUtil;
   
    @Override
    public String authenticate(Participante participant) throws Exception {
        
        // 1. Determinar si existe una instancia de Participante con el mismo email
        // (Usamos el servicio de participante que ya tenías)
        Participante foundParticipant = participantService.findByEmail(participant.getEmail());
        
        if (foundParticipant == null) {
            throw new UserNotFoundException("Email incorrecto.");
        }

        // 2. Utilizar PasswordEncoder para verificar el password
        boolean passwordMatches = passwordEncoder.verify(
            participant.getPassword(),      // password en texto plano del DTO
            foundParticipant.getPassword()  // password hasheado de la BD
        );
        
        if (!passwordMatches) {
            throw new AuthenticationFailedException("Contraseña incorrecta.");
        }

        // 3. Generar un token JWT y retornarlo
        return jwtTokenUtil.generateToken(foundParticipant.getEmail());
    }

    @Override
    public String authenticate(Administrador administrador) throws Exception {
        Administrador foundAdmin = adminService.findByEmail(administrador.getEmail());
        
        if (foundAdmin == null) {
            throw new UserNotFoundException("Email incorrecto.");
        }

        boolean passwordMatches = passwordEncoder.verify(
            administrador.getPassword(),
            foundAdmin.getPassword()
        );
        
        if (!passwordMatches) {
            throw new AuthenticationFailedException("Contraseña incorrecta.");
        }

        return jwtTokenUtil.generateToken(foundAdmin.getEmail());
    }
}