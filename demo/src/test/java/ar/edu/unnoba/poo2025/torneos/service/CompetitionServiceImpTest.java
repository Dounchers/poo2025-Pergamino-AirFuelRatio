package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.exception.*;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.dto.CompetitionResponseDTO;
import ar.edu.unnoba.poo2025.torneos.repository.CompetitionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetitionServiceImpTest {

    @Mock
    private CompetitionRepository competitionRepository;
    @Mock
    private TournamentService tournamentService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CompetitionServiceImp competitionServiceImp;

    //Datos de prueba
    private final Long TOURNAMENT_ID = 1L;

    //TEST PARA GET COMPETITIONS BY TOURNAMENT ID
    @Test
    void shouldReturnTournamentNotFoundExceptionWhenTournamentDoesNotExist() {
        when(tournamentService.findById(TOURNAMENT_ID)).thenReturn(null);

        Exception exception = assertThrows(TournamentNotFoundException.class, () -> {
            competitionServiceImp.findByTournamentId(TOURNAMENT_ID);
        },"debería lanzar TournamentNotFoundException si el torneo que retorna es null (no existe)");

        //verificamos que se llamó al método findById del servicio de torneos
        verify(tournamentService, times(1)).findById(TOURNAMENT_ID);

        //pero verificamos que no se llamó al repositorio de competencias (ya que no existe el torneo)
        verify(competitionRepository, never()).findByTorneoId(any());
    }
    @Test
    void shouldReturnTournamentNotPublishedExceptionWhenTournamentIsNotPublished(){
        Torneo notPublishedTournament = new Torneo();
        notPublishedTournament.setPublish(false);

        when(tournamentService.findById(TOURNAMENT_ID)).thenReturn(notPublishedTournament);

        Exception exception = assertThrows(TournamentNotPublishedException.class, () -> {
            competitionServiceImp.findByTournamentId(TOURNAMENT_ID);
        }, "debería lanzar TournamentNotPublishedException si el torneo que retorna tiene atributo publish en false");

        verify(tournamentService, times(1)).findById(TOURNAMENT_ID);
        verify(competitionRepository, never()).findByTorneoId(any());

    }

    @Test
    void shouldReturnTournamentCompetitionsWhenTournamentIsPublished() {
        //Mockeamos un torneo publicado
        Torneo publishedTournament = new Torneo();
        publishedTournament.setPublish(true);
        publishedTournament.setName("Torneo Válido Test");

        //mockeamos las competencias asociadas al torneo
        Competencia mockedCompetition = new Competencia();
        mockedCompetition.setTorneo(publishedTournament); // importante asociar el torneo a la competencia

        // mockeamos una lista de Competencias (lo que retornaría la capa de Repositorio)
        //con tener una sola competencia es suficiente para este test, principi ode minimo esfuerzo
        List<Competencia> mockCompetitions = Collections.singletonList(mockedCompetition);

        // Configuramos el mock
        // El servicio de torneo devuelve el torneo que existe y está publicado
        when(tournamentService.findById(TOURNAMENT_ID)).thenReturn(publishedTournament);
        // el repositorio de competencias devuelve la lista mockeada
        when(competitionRepository.findByTorneoId(TOURNAMENT_ID)).thenReturn(mockCompetitions);

        // simulamos también el modelMapper
        when(modelMapper.map(any(), eq(CompetitionResponseDTO.class))).thenReturn(new CompetitionResponseDTO());

        // no deberías altar ninguna excepción

        assertDoesNotThrow(() -> {
            competitionServiceImp.findByTournamentId(TOURNAMENT_ID);
        });

        //en este caso si verificamos que el competitionRepository haya sido llamado
        verify(tournamentService, times(1)).findById(TOURNAMENT_ID); // Debe llamarse 1 vez
        verify(competitionRepository, times(1)).findByTorneoId(TOURNAMENT_ID); // Debe llamarse 1 vez
    }

    //TEST PARA CREATE
    @Test
    void shouldNotCreateCompetitionWhenTournamentDoesNotExist() {
        when(tournamentService.findById(TOURNAMENT_ID)).thenReturn(null);

        Exception exception = assertThrows(TournamentNotFoundException.class, () -> {
            competitionServiceImp.create(TOURNAMENT_ID, new Competencia());
        }, "debería lanzar TournamentNotFoundException si el torneo que retorna es null (no existe)");

        //verificamos que se llamó al método findById del servicio de torneos
        verify(tournamentService, times(1)).findById(TOURNAMENT_ID);

        //pero verificamos que no se llamó al repositorio de competencias (ya que no existe el torneo)
        verify(competitionRepository, never()).save(any());
    }

    @Test
    void shouldNotCreateCompetitionWhenTournamentIsPublished(){
        Torneo publishedTournament = new Torneo();
        publishedTournament.setPublish(true);
        when(tournamentService.findById(TOURNAMENT_ID)).thenReturn(publishedTournament);

        Exception exception = assertThrows(TournamentAlreadyPublishedException.class, () -> {
            competitionServiceImp.create(TOURNAMENT_ID, new Competencia());
        }, "debería lanzar la excepción cuando el torneo está publicado");

        verify(tournamentService, times(1)).findById(TOURNAMENT_ID);
        verify(competitionRepository, never()).save(any());
    }

    @Test
    void shouldCreateCompetitionWhenTournamentExistsAndIsNotPublished() throws Exception {
        Torneo publishedTournament = new Torneo();
        publishedTournament.setPublish(false);

        Competencia newCompetition = new Competencia();
        newCompetition.setName("Competencia de Prueba");

        when(tournamentService.findById(TOURNAMENT_ID)).thenReturn(publishedTournament);
        when(competitionRepository.save(any(Competencia.class))).thenReturn(newCompetition);

        Competencia createdCompetition = competitionServiceImp.create(TOURNAMENT_ID, newCompetition);

        verify(tournamentService, times(1)).findById(TOURNAMENT_ID);
        verify(competitionRepository, times(1)).save(any(Competencia.class));
    }

    //TEST PARA UPDATE

    @Test
    void shouldReturnCompetitionNotFoundExceptionWhenUpdatingNonExistentCompetition() {
        Long competitionId = 1L;
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CompetitionNotFoundException.class, () -> {
            competitionServiceImp.update(competitionId, new Competencia());
        }, "debería lanzar CompetitionNotFoundException si se intenta actualizar una competencia que no existe");

        //verificamos que se llamó al método findById del repositorio de competencias
        verify(competitionRepository, times(1)).findById(competitionId);
        //pero verificamos que no se llamó al método save del repositorio de competencias
        verify(competitionRepository, never()).save(any());
    }

    @Test
    void shouldReturnTournamentAlreadyPublishedExceptionWhenUpdatingCompetitionOfPublishedTournament() {
        Long competitionId = 1L;
        //mockeamoas el torneo publicado
        Torneo publishedTournament = new Torneo();
        publishedTournament.setPublish(true);

        //mockeamos la competencia existente
        Competencia existingCompetition = new Competencia();
        existingCompetition.setTorneo(publishedTournament);

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(existingCompetition));

        Exception exception = assertThrows(TournamentAlreadyPublishedException.class, () -> {
            competitionServiceImp.update(competitionId, new Competencia());
        }, "debería lanzar TournamentAlreadyPublishedException si se intenta actualizar una competencia de un torneo publicado");

        verify(competitionRepository, times(1)).findById(competitionId);
        verify(competitionRepository, never()).save(any());
    }

    @Test
    void shouldUpdateCompetitionWhenValid() throws Exception {
        Long competitionId = 1L;
        //mockeamoas el torneo no publicado
        Torneo notPublishedTournament = new Torneo();
        notPublishedTournament.setPublish(false);

        //mockeamos la competencia existente
        Competencia existingCompetition = new Competencia();
        existingCompetition.setTorneo(notPublishedTournament);

        Competencia updatedData = new Competencia();
        updatedData.setName("Nueva Competencia");
        updatedData.setCapacity(100);
        updatedData.setBasePrice(BigDecimal.valueOf(50.0));

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(existingCompetition));
        when(competitionRepository.save(any(Competencia.class))).thenReturn(existingCompetition);

        Competencia updatedCompetition = competitionServiceImp.update(competitionId, updatedData);

        verify(competitionRepository, times(1)).findById(competitionId);
        verify(competitionRepository, times(1)).save(existingCompetition);

        assertEquals("Nueva Competencia", updatedCompetition.getName());
        assertEquals(100, updatedCompetition.getCapacity());
        assertEquals(BigDecimal.valueOf(50.0), updatedCompetition.getBasePrice());
    }

    //TEST PARA DELETE
    @Test
    void shouldReturnCompetitionNotFoundExceptionWhenDeletingNonExistentCompetition() {
        Long competitionId = 1L;
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CompetitionNotFoundException.class, () -> {
            competitionServiceImp.update(competitionId, new Competencia());
        }, "debería lanzar CompetitionNotFoundException si se intenta eliminar una competencia que no existe");

        //verificamos que se llamó al método findById del repositorio de competencias
        verify(competitionRepository, times(1)).findById(competitionId);
        //pero verificamos que no se llamó al método save del repositorio de competencias
        verify(competitionRepository, never()).delete(any());
    }
    @Test
    void shouldReturnTournamentAlreadyPublishedExceptionWhenDeletingCompetitionOfPublishedTournament() {
        Long competitionId = 1L;
        //mockeamoas el torneo publicado
        Torneo publishedTournament = new Torneo();
        publishedTournament.setPublish(true);

        //mockeamos la competencia existente
        Competencia existingCompetition = new Competencia();
        existingCompetition.setTorneo(publishedTournament);

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(existingCompetition));

        Exception exception = assertThrows(TournamentAlreadyPublishedException.class, () -> {
            competitionServiceImp.update(competitionId, new Competencia());
        }, "debería lanzar TournamentAlreadyPublishedException si se intenta eliminar una competencia de un torneo publicado");

        verify(competitionRepository, times(1)).findById(competitionId);
        verify(competitionRepository, never()).delete(any());
    }

    @Test
    void shouldDeleteCompetitionWhenValid() throws Exception {
        Long competitionId = 1L;
        //mockeamoas el torneo no publicado
        Torneo notPublishedTournament = new Torneo();
        notPublishedTournament.setPublish(false);

        //mockeamos la competencia existente
        Competencia existingCompetition = new Competencia();
        existingCompetition.setTorneo(notPublishedTournament);

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(existingCompetition));

        assertDoesNotThrow(() -> {
            competitionServiceImp.delete(competitionId);
        }, "No debería lanzar ninguna excepción al eliminar una competencia válida");

        verify(competitionRepository, times(1)).findById(competitionId);
        verify(competitionRepository, times(1)).delete(existingCompetition);
    }

    //TEST PARA FIND BY ID AND TOURNAMENT ID
    @Test
    void shouldReturnTournamentNotFoundExceptionWhenFindingIdTournamentDoesNotExist() {
        Long competitionId = 1L;

        when(tournamentService.findById(TOURNAMENT_ID)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            competitionServiceImp.findByIdAndTorneoId(competitionId, TOURNAMENT_ID);
        }, "debería lanzar Exception si el torneo que retorna es null (no existe)");

        verify(tournamentService, times(1)).findById(TOURNAMENT_ID);
        verify(competitionRepository, never()).findByIdAndTorneoId(any(), any());
    }

    @Test
    void shouldReturnTournamentNotPublishedExceptionWhenFindingIdTournamentIsNotPublished(){
        Torneo notPublishedTournament = new Torneo();
        notPublishedTournament.setPublish(false);

        when(tournamentService.findById(TOURNAMENT_ID)).thenReturn(notPublishedTournament);

        Exception exception = assertThrows(TournamentNotPublishedException.class, () -> {
            competitionServiceImp.findByIdAndTorneoId(1L, TOURNAMENT_ID);
        }, "debería lanzar Exception si el torneo que busca no está publicado");

        verify(tournamentService, times(1)).findById(TOURNAMENT_ID);
        verify(competitionRepository, never()).findByIdAndTorneoId(any(), any());
    }

    @Test
    void shouldReturnCompetitionNotFoundWhenCompetitionNotInTournament(){
        Long competitionId = 1L;

        Torneo publishedTournament = new Torneo();
        publishedTournament.setPublish(true);

        when(tournamentService.findById(TOURNAMENT_ID)).thenReturn(publishedTournament);
        when(competitionRepository.findByIdAndTorneoId(competitionId, TOURNAMENT_ID)).thenReturn(null);

        Exception exception = assertThrows(CompetitionNotFoundException.class, () -> {
            competitionServiceImp.findByIdAndTorneoId(competitionId, TOURNAMENT_ID);
        }, "debería lanzar Exception si la competencia no se encuentra en el torneo especificado");

        verify(tournamentService, times(1)).findById(TOURNAMENT_ID);
        verify(competitionRepository, times(1)).findByIdAndTorneoId(competitionId, TOURNAMENT_ID);
    }

    @Test
    void shouldReturnCompetitionResponseDTOWhenValid(){
        Long competitionId = 1L;

        Torneo publishedTournament = new Torneo();
        publishedTournament.setPublish(true);
        publishedTournament.setName("Torneo Válido Test");

        Competencia existingCompetition = new Competencia();
        existingCompetition.setTorneo(publishedTournament);

        CompetitionResponseDTO responseDTO = new CompetitionResponseDTO();

        when(tournamentService.findById(TOURNAMENT_ID)).thenReturn(publishedTournament);
        when(competitionRepository.findByIdAndTorneoId(competitionId, TOURNAMENT_ID)).thenReturn(existingCompetition);
        when(modelMapper.map(existingCompetition, CompetitionResponseDTO.class)).thenReturn(responseDTO);

        assertDoesNotThrow(() -> {
            CompetitionResponseDTO result = competitionServiceImp.findByIdAndTorneoId(competitionId, TOURNAMENT_ID);
            assertEquals(responseDTO, result, "debería retornar el DTO mapeado correctamente");
        });

        verify(tournamentService, times(1)).findById(TOURNAMENT_ID);
        verify(competitionRepository, times(1)).findByIdAndTorneoId(competitionId, TOURNAMENT_ID);
        verify(modelMapper, times(1)).map(existingCompetition, CompetitionResponseDTO.class);
    }

}