package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.model.Participante;

//Interface que define el servicio de operaciones relacionadas con la entidad Participante.
public interface ParticipantService {

  //Metodo para crear un nuevo Participante.
  public void create(Participante participante) throws Exception;
}
