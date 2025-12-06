package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.UnauthorizedException;
import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import ar.edu.unnoba.poo2025.torneos.repository.AdminRepository;
import ar.edu.unnoba.poo2025.torneos.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthorizationService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private AdminRepository adminRepository;

    public void authorize(String token) {
        if (!jwtTokenUtil.verify(token)) {
            throw new InvalidTokenException("Token inválido");
        }
        String email = jwtTokenUtil.getSubject(token);
        Administrador admin = adminRepository.findByEmail(email);
        if (admin == null) {
            throw new UnauthorizedException("Acceso denegado: El usuario no es administrador");
        }
    }
}
