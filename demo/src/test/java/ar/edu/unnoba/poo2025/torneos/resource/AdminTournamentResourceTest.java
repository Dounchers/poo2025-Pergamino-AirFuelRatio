package ar.edu.unnoba.poo2025.torneos.resource;

import ar.edu.unnoba.poo2025.torneos.dto.*;
import ar.edu.unnoba.poo2025.torneos.exception.InvalidDateRangeException;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.model.Usuario;
import ar.edu.unnoba.poo2025.torneos.service.CompetitionService;
import ar.edu.unnoba.poo2025.torneos.service.TournamentService;
import ar.edu.unnoba.poo2025.torneos.util.AdminValidator;
import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminTournamentResource.class)
public class AdminTournamentResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TournamentService tournamentService;

    @MockBean
    private CompetitionService competitionService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private AdminValidator adminValidator;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Administrador adminUser;

    @BeforeEach
    void setUp() {
        token = "Bearer admin-token";
        adminUser = mock(Administrador.class);
        when(adminUser.getEmail()).thenReturn("admin@unnoba.edu.ar");
        when(adminValidator.validate(anyString())).thenReturn(adminUser);
    }

    // --- TEST TORNEOS ---

    @Test
    void getTournaments_ShouldReturnList() throws Exception {
        // Arrange
        Torneo t1 = new Torneo();
        t1.setId(1L);
        t1.setDateStart(LocalDate.now());
        
        List<Torneo> torneos = Arrays.asList(t1);
        TournamentListResponseDTO dto = new TournamentListResponseDTO();
        dto.setId(1L);

        when(tournamentService.findAll()).thenReturn(torneos);
        when(modelMapper.map(any(Torneo.class), eq(TournamentListResponseDTO.class))).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/admin/tournaments")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getTournamentById_WhenExists_ShouldReturnDetails() throws Exception {
        // Arrange
        Long id = 1L;
        Torneo torneo = new Torneo();
        torneo.setId(id);
        torneo.setName("Torneo Java");
        
        when(tournamentService.findById(id)).thenReturn(torneo);
        when(tournamentService.getTotalEnrollments(id)).thenReturn(10L);
        when(tournamentService.getTotalRevenue(id)).thenReturn(BigDecimal.valueOf(1000.0));

        // Act & Assert
        mockMvc.perform(get("/admin/tournaments/{id}", id)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.totalEnrollments").value(10))
                .andExpect(jsonPath("$.totalRevenue").value(1000.0));
    }

    @Test
    void getTournamentById_WhenNotExists_ShouldReturn404() throws Exception {
        when(tournamentService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/admin/tournaments/{id}", 99L)
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTournament_WhenValid_ShouldReturn201() throws Exception {
        CreateTournamentRequestDTO request = new CreateTournamentRequestDTO();
        request.setName("Nuevo Torneo");
        // Asumiendo que el DTO tiene setters o constructor

        Torneo torneoMapeado = new Torneo();
        when(modelMapper.map(any(CreateTournamentRequestDTO.class), eq(Torneo.class))).thenReturn(torneoMapeado);
        doNothing().when(tournamentService).create(any(Torneo.class));

        mockMvc.perform(post("/admin/tournaments")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createTournament_WhenDateRangeInvalid_ShouldReturn400() throws Exception {
        CreateTournamentRequestDTO request = new CreateTournamentRequestDTO();
        Torneo torneo = new Torneo();
        
        when(modelMapper.map(any(), eq(Torneo.class))).thenReturn(torneo);
        doThrow(new InvalidDateRangeException("Fechas invalidas")).when(tournamentService).create(any());

        mockMvc.perform(post("/admin/tournaments")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Fechas invalidas"));
    }

    @Test
    void deleteTournament_ShouldReturn200() throws Exception {
        doNothing().when(tournamentService).delete(1L);

        mockMvc.perform(delete("/admin/tournaments/{id}", 1L)
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void publishTournament_ShouldReturn200() throws Exception {
        doNothing().when(tournamentService).publish(1L);

        mockMvc.perform(patch("/admin/tournaments/{id}/published", 1L)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Torneo publicado"));
    }

    // --- TEST COMPETENCIAS ---

    @Test
    void getCompetitions_ShouldReturnList() throws Exception {
        Long tourId = 1L;
        when(competitionService.findByTournamentId(tourId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/tournaments/{tournamentId}/competitions", tourId)
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void createCompetition_ShouldReturn201() throws Exception {
        Long tourId = 1L;
        CreateCompetitionDTO dto = new CreateCompetitionDTO();
        dto.setName("Futbol 5");
        dto.setCupo(10);
        dto.setPrecio(BigDecimal.TEN);

        Competencia compMapeada = new Competencia();
        Competencia compCreada = new Competencia();
        compCreada.setId(10L);

        when(modelMapper.map(any(CreateCompetitionDTO.class), eq(Competencia.class))).thenReturn(compMapeada);
        when(competitionService.create(eq(tourId), any(Competencia.class))).thenReturn(compCreada);
        when(modelMapper.map(eq(compCreada), eq(CreateCompetitionDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/admin/tournaments/{tournamentId}/competitions", tourId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateCompetition_ShouldReturn200() throws Exception {
        Long compId = 5L;
        CreateCompetitionDTO dto = new CreateCompetitionDTO();
        dto.setName("Updated");
        
        Competencia updatedComp = new Competencia();
        updatedComp.setName("Updated");

        when(competitionService.update(eq(compId), any(Competencia.class))).thenReturn(updatedComp);

        mockMvc.perform(put("/admin/tournaments/1/competitions/{id}", compId) // ID del torneo en URL es irrelevante para este endpoint segun tu codigo, pero debe estar
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }
}