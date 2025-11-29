package ar.edu.unnoba.poo2025.torneos.service;

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

    public void authorize(String token) throws Exception {
        if (!jwtTokenUtil.verify(token)) {
            throw new Exception("Token inválido");
        }
        String email = jwtTokenUtil.getSubject(token);
        Administrador admin = adminRepository.findByEmail(email);
        if (admin == null) {
            throw new Exception("Acceso denegado: El usuario no es administrador");
        }
    }
}
