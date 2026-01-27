package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.exception.*;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.repository.CompetitionRepository;
import ar.edu.unnoba.poo2025.torneos.repository.InscriptionRepository;
import ar.edu.unnoba.poo2025.torneos.repository.ParticipantRepository;
import ar.edu.unnoba.poo2025.torneos.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscriptionServiceImpTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private CompetitionRepository competitionRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private InscriptionRepository inscriptionRepository;

    @InjectMocks
    private InscriptionServiceImp inscriptionService;

    private final long tournamentId = 1L;
    private final long competitionId = 10L;
    private final long participantId = 101L;

    //TEST PARA inscribeParticipant
    @Test
    void shouldReturnTournamentNotFoundExceptionWhenTournamentDoesNotExist() {
        //mockeamos el fallo de búsqueda
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TournamentNotFoundException.class, () -> {
            inscriptionService.inscribeParticipant(tournamentId, competitionId, participantId);
        }, "Debería lanzar tournament not found exception si el torneo no existe");

        // verificamos que sí haya buscado el torneo
        verify(tournamentRepository, times(1)).findById(tournamentId);

        // verificamos que las otras búsquedas no ocurrieron, si no encuentra torneo no debería buscar el resto si es secuencial
        verify(competitionRepository, never()).findById(anyLong());
        verify(participantRepository, never()).findById(anyLong());

        // verificamos que no haya intentado guardar ninguna inscripción
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void shouldReturnCompetitionNotFoundExceptionWhenCompetitionDoesNotExist() {
        //Mockeamos la búsqueda exitosa del torneo
        Torneo existingTournament = new Torneo();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(existingTournament));

        //mockeamos el fallo de búsqueda de competencia
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CompetitionNotFoundException.class, () -> {
            inscriptionService.inscribeParticipant(tournamentId, competitionId, participantId);
        }, "Debería lanzar competition not found exception si la competencia no existe");

        // verificamos que sí haya buscado la competencia y el torneo
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(competitionRepository, times(1)).findById(competitionId);

        // verificamos que las otras búsquedas no ocurrieron, si no encuentra competencia no debería buscar el resto si es secuencial
        verify(participantRepository, never()).findById(anyLong());

        // verificamos que no haya intentado guardar ninguna inscripción
        verify(inscriptionRepository, never()).save(any());

    }

    @Test
    void shouldReturnParticipantNotFoundExceptionWhenParticipantDoesNotExist() {
        //Mockeamos la búsqueda exitosa del torneo
        Torneo existingTournament = new Torneo();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(existingTournament));

        //Mockeamos la búsqueda exitosa de la competencia
        Competencia existingCompetition = new Competencia();
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(existingCompetition));

        //mockeamos el fallo de búsqueda de participante
        when(participantRepository.findById(participantId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ParticipantNotFoundException.class, () -> {
            inscriptionService.inscribeParticipant(tournamentId, competitionId, participantId);
        }, "Debería lanzar participant not found exception si el participante no existe");

        // verificamos que sí haya buscado el participante y los anteriores
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(competitionRepository, times(1)).findById(competitionId);
        verify(participantRepository, times(1)).findById(participantId);

        // verificamos que no haya intentado guardar ninguna inscripción
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void shouldReturnTournamentNotFoundExceptionWhenCompetitionDoesNotBelongToTournament() {
        // simulamos ids de torneo
        Long validTournamentId = 1L;
        Long differentTournamentId = 999L; // ID simulado al que apunta la Competencia

        //mockeamos la búsqueda exitosa del torneo y el participante
        Torneo existingTournament = new Torneo();
        existingTournament.setId(validTournamentId);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(existingTournament));
        Participante existingParticipant = new Participante();
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(existingParticipant));

        //Mockeamos un objeto Competencia con un torneo diferente
        Torneo differentTournament = new Torneo();
        differentTournament.setId(differentTournamentId); // ID diferente al tournamentId

        Competencia existingCompetition = new Competencia();
        existingCompetition.setTorneo(differentTournament); // Asignamos el torneo diferente
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(existingCompetition));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            //La ruta pide Torneo 1L, pero la Competencia encontrada apunta a 999L
            inscriptionService.inscribeParticipant(tournamentId, competitionId, participantId);
        }, "Debería lanzar tournament not found exception si la competencia no pertenece al torneo especificado");

        // verificamos que sí haya buscado todos los recursos
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(competitionRepository, times(1)).findById(competitionId);
        verify(participantRepository, times(1)).findById(participantId);

        //verificamos que no haya intentado guardar ninguna inscripción
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void shouldReturnTournamentNotPublishedExceptionWhenTournamentIsNotPublished() {
        //mockeamos la búsqueda exitosa del torneo no publicado
        Torneo notPublishedTournament = new Torneo();
        notPublishedTournament.setId(tournamentId);
        notPublishedTournament.setPublish(false); // Torneo no publicado
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(notPublishedTournament));

        //Mockeamos la búsqueda exitosa de la competencia y el participante
        Competencia existingCompetition = new Competencia();
        existingCompetition.setTorneo(notPublishedTournament);
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(existingCompetition));
        Participante existingParticipant = new Participante();
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(existingParticipant));

        Exception exception = assertThrows(TournamentNotPublishedException.class, () -> {
            inscriptionService.inscribeParticipant(tournamentId, competitionId, participantId);
        }, "Debería lanzar tournament not published exception si el torneo no está publicado y no acepta inscripciones");

        // verificamos que sí haya buscado todos los recursos
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(competitionRepository, times(1)).findById(competitionId);
        verify(participantRepository, times(1)).findById(participantId);

        //verificamos que no haya intentado guardar ninguna inscripción
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void shouldThrowEnrollmentDateExceededExceptionWhenEnrollmentDateIsPast() {
        Torneo torneoValido = new Torneo();
        torneoValido.setPublish(true); // Pasa la verificación previa
        // Configuramos fecha de inicio pasada para el torneo
        torneoValido.setDateStart(LocalDate.now().minusDays(1)); // Fecha de inicio de ayer (ya pasó)
        torneoValido.setId(tournamentId);

        Competencia competenciaValida = new Competencia();
        competenciaValida.setTorneo(torneoValido); // Vinculamos la competencia al torneo
        competenciaValida.setId(competitionId);

        Participante participanteValido = new Participante();

        // Mockeamos la búsqueda exitosa de todos los recursos necesarios
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(torneoValido));
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competenciaValida));
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participanteValido));

        competenciaValida.setTorneo(torneoValido);

        assertThrows(EnrollmentDateExceededException.class, () -> {
            inscriptionService.inscribeParticipant(tournamentId, competitionId, participantId);
        }, "Debe lanzar la excepción si la fecha de inscripción (hoy) es posterior a la fecha de inicio del torneo.");


        // Verificamos que se ejecutó la búsqueda del Torneo para llegar a esta validación
        verify(tournamentRepository, times(1)).findById(tournamentId);

        // Verificamos que el efecto secundario de la base de datos NUNCA ocurrió
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void shouldReturnNoCapacityExceptionWhenCompetitionIsAtFullCapacity() {
        Torneo validTournament = new Torneo();
        validTournament.setPublish(true);
        validTournament.setDateStart(LocalDate.now().plusDays(10));
        validTournament.setId(tournamentId);

        Competencia fullCapacityCompetitionMock = mock(Competencia.class);
        //este es el punto clave del test,
        when(fullCapacityCompetitionMock.hasCapacityAvailable()).thenReturn(false);
        when(fullCapacityCompetitionMock.getTorneo()).thenReturn(validTournament);

        Participante validParticipant = new Participante();

        // Mockeamos la búsqueda exitosa de todos los recursos necesarios
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(validTournament));
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(fullCapacityCompetitionMock));
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(validParticipant));

        assertThrows(NoCapacityException.class, () -> {
            inscriptionService.inscribeParticipant(tournamentId, competitionId, participantId);
        }, "Debe lanzar NoCapacityException si no hay cupo disponible.");

        //verificamos que se ejecutó la búsqueda del Torneo, Competencia y Participante
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(competitionRepository, times(1)).findById(competitionId);
        verify(participantRepository, times(1)).findById(participantId);

        verify(fullCapacityCompetitionMock, times(1)).hasCapacityAvailable();

        //verificamos que no haya intentado guardar ninguna inscripción
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void shouldReturnAlreadyInscribedExceptionWhenParticipantIsAlreadyEnrolledInCompetition() {
        Torneo validTournament = new Torneo();
        validTournament.setPublish(true);
        validTournament.setDateStart(LocalDate.now().plusDays(10));
        validTournament.setId(tournamentId);

        Competencia validCompetitionMock = mock(Competencia.class);
        when(validCompetitionMock.hasCapacityAvailable()).thenReturn(true);
        when(validCompetitionMock.getTorneo()).thenReturn(validTournament);

        Participante validParticipant = new Participante();

        // Mockeamos la búsqueda exitosa de todos los recursos necesarios
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(validTournament));
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(validCompetitionMock));
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(validParticipant));

        when(inscriptionRepository.existsByParticipanteIdAndCompetenciaId(participantId, competitionId))
                .thenReturn(true);

        assertThrows(AlreadyInscribedException.class, () -> {
            inscriptionService.inscribeParticipant(tournamentId, competitionId, participantId);
        }, "Debe lanzar AlreadyInscribedException si el participante ya está inscripto en la competencia.");

        //verificamos que se ejecutó la búsqueda para verificar inscripción previa
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(competitionRepository, times(1)).findById(competitionId);
        verify(participantRepository, times(1)).findById(participantId);

        //verificamos qeu se ejecutó el rpeositorio
        verify(inscriptionRepository, times(1)).existsByParticipanteIdAndCompetenciaId(participantId, competitionId);

        //verificamos que no haya intentado guardar ninguna inscripción
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void shouldEnrollParticipantWithFullPriceWhenItIsTheFirstInscriptionInTournament() {
        Torneo validTournament = new Torneo();
        validTournament.setPublish(true);
        validTournament.setDateStart(LocalDate.now().plusDays(10));
        validTournament.setId(tournamentId);

        // Instancia real con estado y luego la espiamos
        Competencia realCompetition = new Competencia();
        realCompetition.setTorneo(validTournament);
        realCompetition.setBasePrice(BigDecimal.valueOf(20000.0));
        realCompetition.setCapacity(100);
        //utilizamos el spy para poder stubear el método de capacidad, siempre retornará true
        Competencia competitionSpy = spy(realCompetition);
        when(competitionSpy.hasCapacityAvailable()).thenReturn(true);

        Participante validParticipant = new Participante();

        // Mockeos: devolver el spy para que el stub anterior sea utilizado
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(validTournament));
        //si no usamos el spy, el método hasCapacityAvailable() no se puede mockear y salta por esa excepción
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competitionSpy));
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(validParticipant));

        // simulamos que no existe inscripción previa en otras competencias del torneo
        when(inscriptionRepository.existsByParticipanteIdAndTorneoIdExcludingCompetition(participantId, tournamentId, competitionId))
                .thenReturn(false);

        // simular guardado de inscripción
        when(inscriptionRepository.save(any(Inscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Inscripcion savedInscripcion = inscriptionService.inscribeParticipant(tournamentId, competitionId, participantId);

        assertNotNull(savedInscripcion, "La inscripción guardada no debe ser nula.");
        assertNotNull(savedInscripcion.getPrice(), "El precio de la inscripción no debe ser nulo.");
        assertEquals(0, savedInscripcion.getPrice().compareTo(BigDecimal.valueOf(20000.0)),
                "El precio de la inscripción debe ser el precio completo sin descuento.");

        // verificaciones
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(competitionRepository, times(1)).findById(competitionId);
        verify(participantRepository, times(1)).findById(participantId);
        verify(inscriptionRepository, times(1))
                .existsByParticipanteIdAndTorneoIdExcludingCompetition(participantId, tournamentId, competitionId);
        verify(inscriptionRepository, times(1)).save(any(Inscripcion.class));
    }

    @Test
    void shouldEnrollParticipantWithDiscountedPriceWhenItIsNotTheFirstInscriptionInTournament() {
        Torneo validTournament = new Torneo();
        validTournament.setPublish(true);
        validTournament.setDateStart(LocalDate.now().plusDays(10));
        validTournament.setId(tournamentId);

        // Instancia real con estado y luego la espiamos
        Competencia realCompetition = new Competencia();
        realCompetition.setTorneo(validTournament);
        realCompetition.setBasePrice(BigDecimal.valueOf(20000.0));
        realCompetition.setCapacity(100);
        //utilizamos el spy para poder stubear el método de capacidad, siempre retornará true
        Competencia competitionSpy = spy(realCompetition);
        when(competitionSpy.hasCapacityAvailable()).thenReturn(true);

        Participante validParticipant = new Participante();

        // Mockeos: devolver el spy para que el stub anterior sea utilizado
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(validTournament));
        //si no usamos el spy, el método hasCapacityAvailable() no se puede mockear y salta por esa excepción
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competitionSpy));
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(validParticipant));

        // simulamos que ya existe inscripción previa en otras competencias del torneo
        when(inscriptionRepository.existsByParticipanteIdAndTorneoIdExcludingCompetition(participantId, tournamentId, competitionId))
                .thenReturn(true);

        // simular guardado de inscripción
        when(inscriptionRepository.save(any(Inscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Inscripcion savedInscripcion = inscriptionService.inscribeParticipant(tournamentId, competitionId, participantId);

        assertNotNull(savedInscripcion, "La inscripción guardada no debe ser nula.");
        assertNotNull(savedInscripcion.getPrice(), "El precio de la inscripción no debe ser nulo.");
        assertEquals(0, savedInscripcion.getPrice().compareTo(BigDecimal.valueOf(10000.0)),
                "El precio de la inscripción debe ser el precio con descuento del 50%.");

        // verificaciones
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(competitionRepository, times(1)).findById(competitionId);
        verify(participantRepository, times(1)).findById(participantId);
        verify(inscriptionRepository, times(1))
                .existsByParticipanteIdAndTorneoIdExcludingCompetition(participantId, tournamentId, competitionId);
        verify(inscriptionRepository, times(1)).save(any(Inscripcion.class));
    }

    @Test
    void findInscriptionsByParticipant() {

    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenInscriptionIsNotFoundOrDoesNotBelongToParticipant() {
        long inscriptionId = 60L;

        // Configuramos el Mock para simular el fallo de seguridad:
        // La consulta optimizada (que verifica ambos IDs) devuelve Optional vacío.
        when(inscriptionRepository.findByIdAndParticipanteIdWithDetails(inscriptionId, participantId))
                .thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> {
            inscriptionService.findInscriptionDetailsByIdAndParticipantId(inscriptionId, participantId);
        }, "Debe lanzar ResourceNotFoundException para prevenir el acceso no autorizado (IDOR).");

        // verificamos que se ejecutó el repo
        verify(inscriptionRepository, times(1)).findByIdAndParticipanteIdWithDetails(inscriptionId, participantId);
    }

    @Test
    void shouldReturnInscriptionDetailsWhenFoundAndBelongsToParticipant() {
        long inscriptionId = 70L;

        Participante participant = new Participante();

        Inscripcion existingInscription = new Inscripcion();
        existingInscription.setId(inscriptionId);
        existingInscription.setParticipante(participant);

        // Configuramos el Mock para simular que la consulta optimizada devuelve el objeto
        when(inscriptionRepository.findByIdAndParticipanteIdWithDetails(inscriptionId, participantId))
                .thenReturn(Optional.of(existingInscription));

        Inscripcion foundInscription = inscriptionService.findInscriptionDetailsByIdAndParticipantId(inscriptionId, participantId);

        //verificamos
        assertNotNull(foundInscription, "La inscripción encontrada no debe ser nula.");
        assertEquals(inscriptionId, foundInscription.getId(), "debería devolver el objeto inscripcion correcto");

        verify(inscriptionRepository, times(1)).findByIdAndParticipanteIdWithDetails(inscriptionId, participantId);
    }
}