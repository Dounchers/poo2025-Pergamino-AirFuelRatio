package ar.edu.unnoba.poo2025.torneos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ar.edu.unnoba.poo2025.torneos.model.Participante;

//Interface que extiende JpaRepository para mejorar las operaciones CRUD de la entidad Participante.
public interface ParticipantRepository extends JpaRepository<Participante, Long> {

  //Consulta personalizada para encontrar un Participante por su email.
  @Query("SELECT p FROM Participante p WHERE p.email = :email")
  public Participante findByEmail(@Param("email") String email); //El email se para como parametro para la consulta.

  public Boolean existsByDocumentTypeAndDocument(String documentType, String document);
}
