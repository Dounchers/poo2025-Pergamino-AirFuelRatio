package ar.edu.unnoba.poo2025.torneos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.edu.unnoba.poo2025.torneos.exception.ResourceNotFoundException;
import ar.edu.unnoba.poo2025.torneos.exception.BusinessRuleException;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.repository.TournamentRepository;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidDateRangeException;

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
    public List<Torneo> findAll() {
        return tournamentRepository.findAll();
    }
    
    @Override
    public Torneo create(Torneo torneo) throws Exception {
        //Valida que el intervalo de fechas sea lógico (inicio antes que fin)
        validateTournamentDates(torneo);
        torneo.setPublish(false);
        return tournamentRepository.save(torneo);
    }

    @Override
    public Torneo update(Long id, Torneo torneo) throws Exception {
        // Buscar torneo existente
        Torneo existingTorneo = tournamentRepository.findById(id)
            .orElseThrow(() -> new Exception("Torneo no encontrado"));
        
        // Valida la regla de negocio, si se puede editar o no.(Depende si esta publicado o no)
        if (!existingTorneo.isEditable()) {
            throw new Exception("No se puede editar un torneo publicado");
        }
        
        //Valida que el intervalo de fechas sea lógico (inicio antes que fin)
        validateTournamentDates(torneo);
        

        // Actualiza solo los campos del DTO
        existingTorneo.setName(torneo.getName());
        existingTorneo.setDescription(torneo.getDescription());
        existingTorneo.setDateStart(torneo.getDateStart());
        existingTorneo.setDateEnd(torneo.getDateEnd());
        
        // Guardar
        return tournamentRepository.save(existingTorneo);
    }
    private void validateTournamentDates(Torneo torneo) { 
        if (torneo.getDateStart().isAfter(torneo.getDateEnd())) {
            throw new InvalidDateRangeException("La fecha de inicio no puede ser posterior a la fecha de fin"); 
        }
    }

    @Override
    public Long getTotalEnrollments(Long tournamentId) throws Exception{
        Torneo torneo = findById(tournamentId);

        if (torneo == null) {
            throw new Exception("Torneo no encontrado");
        }

        //Cuenta las inscripciones de todas las competencias
        return torneo.getCompetencias().stream()
            .mapToLong(competencia -> competencia.getInscripciones().size())
            .sum();
    }   

    @Override
    public BigDecimal getTotalRevenue(Long tournamentId) throws Exception{
        Torneo torneo = findById(tournamentId);

        if (torneo == null) {
            throw new Exception("Torneo no encontrado");
        }

        //Suma los ingresos de todas las incripciones de todas las competencias de ese torneo
        return torneo.getCompetencias().stream()
            .flatMap(competencia -> competencia.getInscripciones().stream())
            .map(inscripcion -> inscripcion.getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Override
    public void delete(Long id) {
        Torneo torneo = findById(id);
        if (torneo == null) throw new ResourceNotFoundException("Torneo no encontrado");
        if (torneo.getPublish()) throw new BusinessRuleException("No se puede eliminar un torneo publicado");
        tournamentRepository.delete(torneo);
    }

    @Override
    public void publish(Long id) {
        Torneo torneo = findById(id);
        if (torneo == null) throw new ResourceNotFoundException("Torneo no encontrado");
        torneo.setPublish(true);
        tournamentRepository.save(torneo);
    }
}
