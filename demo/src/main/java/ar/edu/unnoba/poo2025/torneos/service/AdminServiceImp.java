package ar.edu.unnoba.poo2025.torneos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.unnoba.poo2025.torneos.exception.DuplicateResourceException;
import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import ar.edu.unnoba.poo2025.torneos.repository.AdminRepository;
import ar.edu.unnoba.poo2025.torneos.util.PasswordEncoder;

@Service
public class AdminServiceImp implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Administrador create(Administrador administrador){
        
        // Verifica si ya existe un administrador con el mismo email
        if (adminRepository.findByEmail(administrador.getEmail()) != null) {
            throw new DuplicateResourceException("El mail ya se encuentra registrado");
        }

        // Cifra la contraseña antes de guardar
        administrador.setPassword(passwordEncoder.encode(administrador.getPassword()));

        // Guarda el nuevo administrador en la base de datos
        return adminRepository.save(administrador);
    }

    @Override
    public Administrador findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public List<Administrador> findAll() {
        return adminRepository.findAll();
    }

    @Override  
    public void delete(Long id) {
        adminRepository.deleteById(id);
    }

    @Override
    public Administrador findById(Long id) throws Exception {
        return adminRepository.findById(id)
            .orElseThrow(() -> new Exception("Administrador no encontrado"));
    }
}