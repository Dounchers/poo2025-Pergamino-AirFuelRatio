package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.exception.DocumentAlreadyRegisteredException;
import ar.edu.unnoba.poo2025.torneos.exception.EmailAlreadyRegisteredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.repository.ParticipantRepository;
import ar.edu.unnoba.poo2025.torneos.util.PasswordEncoder;

import java.util.List;

//La clase implementa la interfaz ParticipantService, proporcionando la lógica real del servicio.
@Service
public class ParticipantServiceImp implements ParticipantService {
  
  @Autowired
  private ParticipantRepository participantRepository; //Repositorio para interactuar con la base de datos.

  @Autowired
  private PasswordEncoder passwordEncoder; //Para cifrar las contraseñas antes de guardarlas.

  //Implementacion del método create desde la interfaz ParticipantService.
  @Override
  public Participante create(Participante participante) throws Exception {

    //Verifica si ya existe un participante con el mismo email en la base de datos.
    if (participantRepository.findByEmail(participante.getEmail()) != null) {
      throw new EmailAlreadyRegisteredException("El email ya se encuentra registrado"); //Si ya existe, lanza una excepción.
    }

    if (participantRepository.existsByDocumentTypeAndDocument(participante.getDocumentType(), participante.getDocument())) {
      throw new DocumentAlreadyRegisteredException("El tipo y número de documento ya están registrados");
    }

    //Si no existe el participante, cifra su contraseña antes de guardarla en la base de datos.
    participante.setPassword(passwordEncoder.encode(participante.getPassword()));

    //Guarda el nuevo participante en la base de datos.
    participantRepository.save(participante);
      return participante;
  }

  @Override
  public List<Participante> findAll() {
      return participantRepository.findAll();
  }

  @Override
  public Participante findByEmail(String email) {
      return participantRepository.findByEmail(email);
  }
}
