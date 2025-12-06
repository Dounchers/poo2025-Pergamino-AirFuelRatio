package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.exception.TournamentAlreadyPublishedException;
import ar.edu.unnoba.poo2025.torneos.exception.TournamentNotFoundException;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceImpTest {
    @Mock
    TournamentRepository tournamentRepository;

    @InjectMocks
    TournamentServiceImp tournamentServiceImp;

    @Test
    void shouldReturnTournamentNotFoundWhenFindingNotExistingTournament() {

        Long tournamentId = 1L;

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        Torneo result = tournamentServiceImp.findById(tournamentId);

        assertNull(result, "Debería retornar null al buscar un torneo no existente");

        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void shouldReturnTournamentWhenFindingExistingTournament() {

        Long tournamentId = 1L;
        Torneo dummyTorneo = mock(Torneo.class);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(dummyTorneo));

        Torneo result = tournamentServiceImp.findById(tournamentId);

        assertNotNull(result, "Debería retornar un torneo al buscar un torneo existente");
        assertEquals(dummyTorneo, result, "El torneo retornado debería ser el mismo que el mockeado");

        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void shouldReturnTournamentNotFoundWhenDeletingNotExistingTournament() {

        Long tournamentId = 1L;

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TournamentNotFoundException.class, () -> {
            tournamentServiceImp.delete(tournamentId);
        }, "Debería lanzar una excepción de torneo no encontrado al tratar de eliminar");

        //verificamos que no se intenta borrar
        verify(tournamentRepository, never()).delete(any());
    }

    @Test
    void shouldReturnTournamentAlreadyPublishedWhenDeletingPublishedTournament() {
        Long tournamentId = 1L;

        Torneo dummyTorneo = mock(Torneo.class);
        when(dummyTorneo.getPublish()).thenReturn(true);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(dummyTorneo));

        assertThrows(TournamentAlreadyPublishedException.class, () -> {
            tournamentServiceImp.delete(tournamentId);
        }, "Debería lanzar una excepción de torneo ya publicado al tratar de eliminar");

        //verificamos que no se intenta borrar
        verify(tournamentRepository, never()).delete(any());
    }

    @Test
    void shouldDeleteTournamentSuccessfully() {
        Long tournamentId = 1L;

        Torneo dummyTorneo = mock(Torneo.class);
        when(dummyTorneo.getPublish()).thenReturn(false);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(dummyTorneo));

        assertDoesNotThrow(() -> {
            tournamentServiceImp.delete(tournamentId);
        }, "No debería lanzar ninguna excepción al eliminar un torneo no publicado");

        //verificamos que se intenta borrar
        verify(tournamentRepository, times(1)).delete(dummyTorneo);
    }

    @Test
    void shouldReturnExceptionWhenPublishingNotExistingTournament() {

        Long tournamentId = 1L;

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TournamentNotFoundException.class, () -> {
            tournamentServiceImp.publish(tournamentId);
        }, "Debería lanzar una excepción de torneo no encontrado al tratar de publicar");

        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void shouldPublishTournamentSuccessfully() {
        Long tournamentId = 1L;

        Torneo dummyTorneo = mock(Torneo.class);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(dummyTorneo));

        assertDoesNotThrow(() -> {
            tournamentServiceImp.publish(tournamentId);
        }, "No debería lanzar ninguna excepción al publicar un torneo existente");

        verify(dummyTorneo, times(1)).setPublish(true);
        verify(tournamentRepository, times(1)).save(dummyTorneo);
    }
}