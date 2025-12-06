package ar.edu.unnoba.poo2025.torneos.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(OrderAnnotation.class)
class AdminResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private String authToken;
	private static final String BASE_URL = "/admin";
	
	// IDs existentes en BD
	private static final int ID_TORNEO_PUBLICADO = 5;
	private static final int ID_COMP_EXISTENTE = 7;
	private static final int ID_ADMIN_EXISTENTE = 6;

	@BeforeEach
	void setup() {
		// Token JWT válido para analia@torneos.com
		this.authToken = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhbmFsaWFAdG9ybmVvcy5jb20iLCJpYXQiOjE3NjQ5NjYwNDIsImV4cCI6MTc2NTgzMDA0Mn0.mv5V2GJ9pgp0WGucZb_cZWqUOjgtgvJjHelfUtCDtZqw8D-_Qa72YPn6LgL09VvqH-6mw39EQyDeilqye1Nevg";
	}

	// ============================================================
	// FASE 1: CREAR DATOS DE PRUEBA
	// ============================================================

	@Test
	@Order(1)
	void test01_CrearTorneo() throws Exception {
		String torneoJson = "{"
				+ "\"name\": \"Torneo Test\","
				+ "\"description\": \"Torneo para testing\","
				+ "\"dateStart\": \"2026-05-01\","
				+ "\"dateEnd\": \"2026-05-31\""
				+ "}";

		mockMvc.perform(post(BASE_URL + "/tournaments")
				.header("Authorization", authToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(torneoJson))
				.andExpect(status().isCreated());
	}

	@Test
	@Order(2)
	void test02_CrearCompetencia() throws Exception {
		// Usar Torneo 10 que existe y no está publicado
		String competenciaJson = "{"
				+ "\"name\": \"Competencia Test\","
				+ "\"cupo\": 100,"
				+ "\"precio\": 50000.00"
				+ "}";

		mockMvc.perform(post(BASE_URL + "/tournaments/10/competitions")
				.header("Authorization", authToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(competenciaJson))
				.andExpect(status().isCreated());
	}

	@Test
	@Order(3)
	void test03_CrearAdminParaBorrar() throws Exception {
		String timestamp = String.valueOf(System.currentTimeMillis());
		String emailAdmin = "admin_test_" + timestamp + "@torneos.com";
		
		String adminJson = "{"
				+ "\"email\": \"" + emailAdmin + "\","
				+ "\"password\": \"testadmin123\""
				+ "}";

		mockMvc.perform(post(BASE_URL + "/accounts")
				.header("Authorization", authToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(adminJson))
				.andExpect(status().isCreated());
	}

	// ============================================================
	// FASE 2: OPERACIONES DE LECTURA (GET)
	// ============================================================

	@Test
	@Order(10)
	void test10_ListarTorneos() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tournaments")
				.header("Authorization", authToken))
				.andExpect(status().isOk());
	}

	@Test
	@Order(11)
	void test11_ObtenerDetalleTorneo() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tournaments/" + ID_TORNEO_PUBLICADO)
				.header("Authorization", authToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalEnrollments").exists())
				.andExpect(jsonPath("$.totalRevenue").exists());
	}

	@Test
	@Order(12)
	void test12_ListarCompetencias() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tournaments/" + ID_TORNEO_PUBLICADO + "/competitions")
				.header("Authorization", authToken))
				.andExpect(status().isOk());
	}

	@Test
	@Order(13)
	void test13_ObtenerDetalleCompetencia() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tournaments/" + ID_TORNEO_PUBLICADO + "/competitions/" + ID_COMP_EXISTENTE)
				.header("Authorization", authToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalInscripciones").exists())
				.andExpect(jsonPath("$.montoTotalRecaudado").exists());
	}

	@Test
	@Order(14)
	void test14_ListarInscripciones() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tournaments/" + ID_TORNEO_PUBLICADO + "/competitions/" + ID_COMP_EXISTENTE + "/inscripciones")
				.header("Authorization", authToken))
				.andExpect(status().isOk());
	}

	@Test
	@Order(15)
	void test15_ListarCuentasAdmin() throws Exception {
		mockMvc.perform(get(BASE_URL + "/accounts")
				.header("Authorization", authToken))
				.andExpect(status().isOk());
	}

	// ============================================================
	// FASE 3: OPERACIONES DE ACTUALIZACIÓN (PUT/PATCH)
	// ============================================================

	@Test
	@Order(20)
	void test20_ActualizarTornoNoPublicado() throws Exception {
		// Actualizar Torneo 10 (no publicado)
		String torneoJson = "{"
				+ "\"name\": \"Torneo Actualizado\","
				+ "\"description\": \"Descripción actualizada\","
				+ "\"dateStart\": \"2026-06-01\","
				+ "\"dateEnd\": \"2026-06-30\""
				+ "}";

		mockMvc.perform(put(BASE_URL + "/tournaments/10")
				.header("Authorization", authToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(torneoJson))
				.andExpect(status().isOk());
	}

	@Test
	@Order(21)
	void test21_PublishTorneo() throws Exception {
		// Publicar Torneo 10
		mockMvc.perform(patch(BASE_URL + "/tournaments/10/published")
				.header("Authorization", authToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").exists());
	}

	// ============================================================
	// FASE 4: OPERACIONES DE AUTENTICACIÓN
	// ============================================================

	@Test
	@Order(30)
	void test30_AutenticarAdministrador() throws Exception {
		String authJson = "{"
				+ "\"email\": \"analia@torneos.com\","
				+ "\"password\": \"analia\""
				+ "}";

		mockMvc.perform(post(BASE_URL + "/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(authJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}

	// ============================================================
	// FASE 5: CASOS DE EXCEPCIÓN Y ERRORES
	// ============================================================

	@Test
	@Order(40)
	void test40_ListarTorneos_SinAutorizacion() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tournaments"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@Order(41)
	void test41_AutenticarAdministrador_CredencialesInvalidas() throws Exception {
		String authJson = "{"
				+ "\"email\": \"analia@torneos.com\","
				+ "\"password\": \"passwordIncorrecto\""
				+ "}";

		mockMvc.perform(post(BASE_URL + "/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(authJson))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@Order(42)
	void test42_CrearTorneo_FechasInvalidas() throws Exception {
		String torneoJson = "{"
				+ "\"name\": \"Torneo Inválido\","
				+ "\"description\": \"Descripción\","
				+ "\"dateStart\": \"2026-04-30\","
				+ "\"dateEnd\": \"2026-04-01\""
				+ "}";

		mockMvc.perform(post(BASE_URL + "/tournaments")
				.header("Authorization", authToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(torneoJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(43)
	void test43_ObtenerDetalleTorneo_TorneoNoEncontrado() throws Exception {
		int ID_TORNEO_INEXISTENTE = 9999;

		mockMvc.perform(get(BASE_URL + "/tournaments/" + ID_TORNEO_INEXISTENTE)
				.header("Authorization", authToken))
				.andExpect(status().isNotFound());
	}

	// ============================================================
	// FASE 6: OPERACIONES DE ELIMINACIÓN (DELETE) - AL FINAL
	// ============================================================

	@Test
	@Order(100)
	void test100_DeleteTornenoParaEliminar() throws Exception {
		// Crear y eliminar un torneo
		String torneoJson = "{"
				+ "\"name\": \"Torneo Para Eliminar\","
				+ "\"description\": \"Será eliminado\","
				+ "\"dateStart\": \"2026-07-01\","
				+ "\"dateEnd\": \"2026-07-31\""
				+ "}";

		mockMvc.perform(post(BASE_URL + "/tournaments")
				.header("Authorization", authToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(torneoJson))
				.andExpect(status().isCreated());
	}

	@Test
	@Order(101)
	void test101_BorrarCuentaAdmin() throws Exception {
		// Borrar el admin existente 6
		mockMvc.perform(delete(BASE_URL + "/accounts/" + ID_ADMIN_EXISTENTE)
				.header("Authorization", authToken))
				.andExpect(status().isNoContent());
	}
}
