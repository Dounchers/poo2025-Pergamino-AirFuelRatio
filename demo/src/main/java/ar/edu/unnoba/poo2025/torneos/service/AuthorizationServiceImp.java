package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationServiceImp implements AuthorizationService {

    private final JwtTokenUtil jwtTokenUtil;
    private final ParticipantService participantService;

    @Autowired
    public AuthorizationServiceImp(JwtTokenUtil jwtTokenUtil, ParticipantService participantService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.participantService = participantService;
    }

    @Override
    public Participante authorize(String token) throws Exception {
        
        // 1. Utilizar JwtTokenUtil verificar validez del token JWT
        if (!jwtTokenUtil.verify(token)) {
            throw new InvalidTokenException("Token JWT inválido o expirado.");
        }

        // 2. Recuperar el subject (email) del token.
        String email = jwtTokenUtil.getSubject(token);
        
        // 3. Utilizar el subject para recuperar instancia de Participant
        Participante participant = participantService.findByEmail(email);

        if (participant == null) {
            throw new UserNotFoundException("Usuario asociado al token no encontrado.");
        }
        
        // 4. Retornar la instancia de Participant
        return participant;
    }
}
