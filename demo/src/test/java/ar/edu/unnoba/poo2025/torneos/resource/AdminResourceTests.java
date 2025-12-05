package ar.edu.unnoba.poo2025.torneos.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AdminResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private String authToken;
	private static final String BASE_URL = "/admin";
	private static final int ID_TORNEO_EXISTENTE = 9;
	private static final int ID_COMP_EXISTENTE = 7;
	private static final int ID_COMP_BORRAR = 11;
	private static final int ID_TORNEO_NO_PUBLICADO = 5;
	private static final int ID_ADMIN_BORRAR = 2;

	@BeforeEach
	void setup() {
		// Token JWT válido obtenido del archivo admin-requests.http
		this.authToken = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhbmFsaWFAdG9ybmVvcy5jb20iLCJpYXQiOjE3NjQ5NjYwNDIsImV4cCI6MTc2NTgzMDA0Mn0.mv5V2GJ9pgp0WGucZb_cZWqUOjgtgvJjHelfUtCDtZqw8D-_Qa72YPn6LgL09VvqH-6mw39EQyDeilqye1Nevg";
	}

	// ============================================================
	// 1. GESTIÓN DE TORNEOS (DELETE / PATCH)
	// ============================================================

	@Test
	void testDeleteTorneo() throws Exception {
		mockMvc.perform(delete(BASE_URL + "/tournaments/" + ID_TORNEO_NO_PUBLICADO)
				.header("Authorization", authToken))
				.andExpect(status().isOk());
	}

	@Test
	void testPublishTorneo() throws Exception {
		mockMvc.perform(patch(BASE_URL + "/tournaments/" + ID_TORNEO_EXISTENTE + "/published")
				.header("Authorization", authToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").exists());
	}

	// ============================================================
	// 2. GESTIÓN DE COMPETENCIAS (CRUD)
	// ============================================================

	@Test
	void testCrearCompetencia() throws Exception {
		String competenciaJson = "{"
				+ "\"name\": \"Carrera Nocturna 2026\","
				+ "\"cupo\": 120,"
				+ "\"precio\": 35000.00"
				+ "}";

		mockMvc.perform(post(BASE_URL + "/tournaments/" + ID_TORNEO_NO_PUBLICADO + "/competitions")
				.header("Authorization", authToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(competenciaJson))
				.andExpect(status().isCreated());
	}

	@Test
	void testActualizarCompetencia() throws Exception {
		String competenciaJson = "{"
				+ "\"name\": \"Carrera Anual Editada\","
				+ "\"cupo\": 200,"
				+ "\"precio\": 40000.00"
				+ "}";

		mockMvc.perform(put(BASE_URL + "/tournaments/" + ID_TORNEO_EXISTENTE + "/competitions/" + ID_COMP_EXISTENTE)
				.header("Authorization", authToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(competenciaJson))
				.andExpect(status().isOk());
	}

	@Test
	void testBorrarCompetencia() throws Exception {
		mockMvc.perform(delete(BASE_URL + "/tournaments/" + ID_TORNEO_EXISTENTE + "/competitions/" + ID_COMP_BORRAR)
				.header("Authorization", authToken))
				.andExpect(status().isOk());
	}

	// ============================================================
	// 3. CONSULTA Y REPORTES DE ADMINISTRACIÓN
	// ============================================================

	@Test
	void testListarCompetencias() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tournaments/" + ID_TORNEO_EXISTENTE + "/competitions")
				.header("Authorization", authToken))
				.andExpect(status().isOk());
	}

	@Test
	void testObtenerDetalleCompetencia() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tournaments/" + ID_TORNEO_EXISTENTE + "/competitions/" + ID_COMP_EXISTENTE)
				.header("Authorization", authToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalInscripciones").exists())
				.andExpect(jsonPath("$.montoTotalRecaudado").exists());
	}

	@Test
	void testListarInscripciones() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tournaments/" + ID_TORNEO_EXISTENTE + "/competitions/" + ID_COMP_EXISTENTE + "/inscripciones")
				.header("Authorization", authToken))
				.andExpect(status().isOk());
	}

	// ============================================================
	// 4. GESTIÓN DE ADMINISTRADORES
	// ============================================================

	@Test
	void testAutenticarAdministrador() throws Exception {
		String authJson = "{"
				+ "\"email\": \"analia@torneos.com\","
				+ "\"password\": \"Analia123\""
				+ "}";

		mockMvc.perform(post(BASE_URL + "/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(authJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}

	@Test
	void testListarCuentasAdmin() throws Exception {
		mockMvc.perform(get(BASE_URL + "/accounts")
				.header("Authorization", authToken))
				.andExpect(status().isOk());
	}

	@Test
	void testCrearCuentaAdmin() throws Exception {
		String adminJson = "{"
				+ "\"email\": \"nuevo_admin@torneos.com\","
				+ "\"password\": \"NuevoAdmin123\""
				+ "}";

		mockMvc.perform(post(BASE_URL + "/accounts")
				.header("Authorization", authToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(adminJson))
				.andExpect(status().isCreated());
	}

	@Test
	void testBorrarCuentaAdmin() throws Exception {
		mockMvc.perform(delete(BASE_URL + "/accounts/" + ID_ADMIN_BORRAR)
				.header("Authorization", authToken))
				.andExpect(status().isNoContent());
	}

	// ============================================================
	// 5. GESTIÓN COMPLETA DE TORNEOS
	// ============================================================

	@Test
	void testListarTorneos() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tournaments")
				.header("Authorization", authToken))
				.andExpect(status().isOk());
	}

	@Test
	void testObtenerDetalleTorneo() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tournaments/" + ID_TORNEO_EXISTENTE)
				.header("Authorization", authToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalEnrollments").exists())
				.andExpect(jsonPath("$.totalRevenue").exists());
	}

	@Test
	void testCrearTorneo() throws Exception {
		String torneoJson = "{"
				+ "\"name\": \"Torneo de Prueba 2026\","
				+ "\"description\": \"Torneo para tests de endpoints\","
				+ "\"dateStart\": \"2026-03-01\","
				+ "\"dateEnd\": \"2026-03-31\""
				+ "}";

		mockMvc.perform(post(BASE_URL + "/tournaments")
				.header("Authorization", authToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(torneoJson))
				.andExpect(status().isCreated());
	}

	@Test
	void testActualizarTorneo() throws Exception {
		String torneoJson = "{"
				+ "\"name\": \"Torneo Editado\","
				+ "\"description\": \"Descripción actualizada\","
				+ "\"dateStart\": \"2026-04-01\","
				+ "\"dateEnd\": \"2026-04-30\""
				+ "}";

		mockMvc.perform(put(BASE_URL + "/tournaments/" + ID_TORNEO_EXISTENTE)
				.header("Authorization", authToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(torneoJson))
				.andExpect(status().isOk());
	}

}
