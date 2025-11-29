package ar.edu.unnoba.poo2025.torneos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.repository.TournamentRepository;

@Service
public class TournamentServiceImp implements TournamentService {
    
    @Autowired
    private TournamentRepository tournamentRepository;

    @Override
    public List<Torneo> getPublishedTournaments() {
        List <Torneo> publishedTournaments = tournamentRepository.findPublishedTrue();
        LocalDate currentDate = LocalDate.now();

        return publishedTournaments.stream()
                .filter(torneo -> torneo.getDateEnd().isAfter(currentDate))
                .collect(Collectors.toList());
    }

    @Override
    public Torneo findById(Long id){
        return tournamentRepository.findById(id).orElse(null);
    }
    @Override
    public void delete(Long id) throws Exception {
        Torneo torneo = findById(id);
        if (torneo == null) throw new Exception("Torneo no encontrado");
        if (torneo.getPublish()) throw new Exception("No se puede eliminar un torneo publicado");
        tournamentRepository.delete(torneo);
    }

    @Override
    public void publish(Long id) throws Exception {
        Torneo torneo = findById(id);
        if (torneo == null) throw new Exception("Torneo no encontrado");
        torneo.setPublish(true);
        tournamentRepository.save(torneo);
    }
}
