package ar.edu.unnoba.poo2025.torneos.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Torneo, Long> {
    
    @Query("SELECT t FROM Torneo t WHERE t.publish = true")
    List<Torneo> findPublishedTrue();
}
