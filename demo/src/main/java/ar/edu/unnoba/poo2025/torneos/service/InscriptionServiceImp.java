package ar.edu.unnoba.poo2025.torneos.service;
import java.util.List;
import org.springframework.stereotype.Service;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.repository.CompetitionRepository;
import ar.edu.unnoba.poo2025.torneos.repository.InscriptionRepository;
import ar.edu.unnoba.poo2025.torneos.repository.ParticipantRepository;
import java.time.LocalDate;
import ar.edu.unnoba.poo2025.torneos.exception.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InscriptionServiceImp implements InscriptionService{
    @Autowired
    private TournamentRepository tournamentRepository;
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private ParticipantRepository participantRepository;
    
    @Autowired
    private InscriptionRepository inscriptionRepository;

    @Override
    public Inscripcion inscribeParticipant(Long tournamentId, Long competitionId, Long participantId) {

        LocalDate today = LocalDate.now();

        // 1. Obtener y Validar Recursos
        Torneo torneo = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado."));

        Competencia competencia = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Competencia no encontrada."));

        Participante participante = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participante no encontrado."));

        // Asegurar que la competencia pertenezca al torneo (Validación de ruta /tournamentId/competitionId)
        if (!competencia.getTorneo().getId().equals(torneo.getId())) {
            throw new ResourceNotFoundException("La competencia no pertenece al torneo especificado.");
        }

        // 2. Aplicar Reglas de Negocio (Llamando a la lógica en las entidades)

        // Regla: Torneo Publicado
        if (!torneo.getPublish()) {
            throw new TournamentNotPublishedException("El torneo no está publicado y no acepta inscripciones.");
        }

        // Regla: Fecha de Inscripción
        if (!torneo.verifyEnrollmentDate(today)) {
            // El método de Torneo ya es robusto: today < dateStart
            throw new EnrollmentDateExceededException("La fecha límite de inscripción ha pasado.");
        }

        // Regla: Cupo Disponible
        if (!competencia.hasCapacityAvailable()) {
            // Se debe asegurar que la lista de inscripciones de la Competencia esté actualizada
            // o usar un método del repository para contar el cupo si no se usa FETCH.EAGER
            throw new NoCapacityException("La competencia ha alcanzado su cupo máximo.");
        }

        // Regla: Única Vez por Competencia
        if (inscriptionRepository.existsByParticipanteIdAndCompetenciaId(participantId, competitionId)) {
            throw new AlreadyInscribedException("El participante ya se encuentra inscripto en esta competencia.");
        }

        // 3. Determinar el Descuento

        // Regla: Descuento 50%
        // Buscamos si el participante ya tiene AL MENOS UNA inscripción en OTRA competencia del MISMO torneo.
        boolean alreadyEnrolledInOtherCompetition = inscriptionRepository
                .existsByParticipanteIdAndTorneoIdExcludingCompetition(participantId, tournamentId, competitionId);

        // El descuento se aplica si *no es* la primera inscripción en el torneo (es decir, si alreadyEnrolledInOtherCompetition es true)
        boolean isFirstEnrollmentInTournament = !alreadyEnrolledInOtherCompetition;

        // 4. Crear y Persistir la Inscripción

        Inscripcion nuevaInscripcion = new Inscripcion();
        nuevaInscripcion.setParticipante(participante);
        nuevaInscripcion.setCompetencia(competencia);
        nuevaInscripcion.setDateEnrollment(today);

        // Calcular y establecer el precio usando la lógica de la entidad Inscripcion
        nuevaInscripcion.calculatePrice(isFirstEnrollmentInTournament);


        return inscriptionRepository.save(nuevaInscripcion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inscripcion> findInscriptionsByParticipant(Long participantId) {
        return inscriptionRepository.findInscriptionsWithDetailsByParticipanteId(participantId);
    }

    @Override
    @Transactional(readOnly = true)
    public Inscripcion findInscriptionDetailsByIdAndParticipantId(Long inscriptionId, Long participantId) {
        // Usamos el método optimizado del repository para evitar el problema N+1
        return inscriptionRepository.findByIdAndParticipanteIdWithDetails(inscriptionId, participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada para el participante especificado."));
}
}
