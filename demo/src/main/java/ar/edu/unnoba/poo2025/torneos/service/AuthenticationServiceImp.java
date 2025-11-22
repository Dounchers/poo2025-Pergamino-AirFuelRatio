package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.util.JwtTokenUtil;
import ar.edu.unnoba.poo2025.torneos.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImp implements AuthenticationService {

    // Usamos la interfaz del servicio, no la implementación
    private final ParticipantService participantService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthenticationServiceImp(ParticipantService participantService, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.participantService = participantService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public String authenticate(Participante participant) throws Exception {
        
        // 1. Determinar si existe una instancia de Participante con el mismo email
        // (Usamos el servicio de participante que ya tenías)
        Participante foundParticipant = participantService.findByEmail(participant.getEmail());
        
        if (foundParticipant == null) {
            throw new Exception("Credenciales inválidas: Email o password incorrecto.");
        }

        // 2. Utilizar PasswordEncoder para verificar el password
        boolean passwordMatches = passwordEncoder.verify(
            participant.getPassword(),      // password en texto plano del DTO
            foundParticipant.getPassword()  // password hasheado de la BD
        );
        
        if (!passwordMatches) {
            throw new Exception("Credenciales inválidas: Email o password incorrecto.");
        }

        // 3. Generar un token JWT y retornarlo
        return jwtTokenUtil.generateToken(foundParticipant.getEmail());
    }
}
