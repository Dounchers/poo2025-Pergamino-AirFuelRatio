package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.model.Participante;

import java.util.List;

//Interface que define el servicio de operaciones relacionadas con la entidad Participante.
public interface ParticipantService {

  //Metodo para crear un nuevo Participante.
  public Participante create(Participante participante) throws Exception;

  //Metodo para obtener todos los participantes
  public List<Participante> findAll();

  public Participante findByEmail(String email);
}
