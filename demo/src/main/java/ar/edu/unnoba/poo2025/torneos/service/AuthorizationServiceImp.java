package ar.edu.unnoba.poo2025.torneos.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.util.JwtTokenUtil;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;

@Service
public class AuthorizationServiceImp implements AuthorizationService {

    @Autowired
    private  JwtTokenUtil jwtTokenUtil;
    @Autowired
    private  ParticipantService participantService;
    @Autowired
    private  AdminService adminService;


    @Override
    public Participante authorize(String token) {
        
        // 1. Utilizar JwtTokenUtil verificar validez del token JWT
        if (!jwtTokenUtil.verify(token)) {
            throw new InvalidTokenException("Token JWT inválido o expirado.");
            throw new InvalidTokenException("Token JWT inválido o expirado.");
        }

        // 2. Recuperar el subject (email) del token.
        String email = jwtTokenUtil.getSubject(token);
        
        // 3. Utilizar el subject para recuperar instancia de Participant
        Participante participant = participantService.findByEmail(email);

        if (participant == null) {
            throw new UserNotFoundException("Participante asociado al token no encontrado.");
        }
        
        // 4. Retornar la instancia de Participant
        return participant;
    }

    @Override
    public Administrador authorizeAdmin(String token) {
        if (!jwtTokenUtil.verify(token)) {
            throw new InvalidTokenException("Token de administrador inválido");
        }
        String email = jwtTokenUtil.getSubject(token);
        Administrador admin = adminService.findByEmail(email);
        if (admin == null) {
            throw new UserNotFoundException("Administrador asociado al token no encontrado.");
        }
        return admin;
    }
}