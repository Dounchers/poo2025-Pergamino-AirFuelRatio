package ar.edu.unnoba.poo2025.torneos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionRepository extends JpaRepository<Competencia, Long>{

  //Buscar todas las competencias asociadas a un torneo por su ID.
  List<Competencia> findByTorneoId(Long TournamentId);

  //Acá se usa métodos de consulta derivada para encontrar una competencia específica por su ID y el ID del torneo asociado.
    //JPA interpreta el nombre del método y genera la consulta correspondiente automáticamente.
    //Para eso se debe asegurar que los nombres de los atributos en el model coincidan con los usados en el método.
  Competencia findByIdAndTorneoId(Long competitionId, Long torneoId);
}
