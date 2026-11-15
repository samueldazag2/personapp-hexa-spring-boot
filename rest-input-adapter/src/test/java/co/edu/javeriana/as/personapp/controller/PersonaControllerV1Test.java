package co.edu.javeriana.as.personapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@ExtendWith(MockitoExtension.class)
class PersonaControllerV1Test {

	private static final String JSON = "{\"dni\":\"123\",\"firstName\":\"Ana\",\"lastName\":\"Perez\",\"age\":\"30\",\"sex\":\"F\",\"database\":\"MARIA\"}";

	@Mock
	private PersonaInputAdapterRest adapter;
	@InjectMocks
	private PersonaControllerV1 controller;

	private MockMvc mockMvc;
	private final PersonaResponse persona = new PersonaResponse("123", "Ana", "Perez", "30", "FEMALE", "MariaDB", "OK");

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new RestExceptionHandler()).build();
	}

	@Test
	void createReturns201() throws Exception {
		when(adapter.crearPersona(any(PersonaRequest.class))).thenReturn(persona);
		mockMvc.perform(post("/api/v1/persona").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.dni").value("123"));
	}

	@Test
	void findAllReturns200() throws Exception {
		when(adapter.historial("maria")).thenReturn(List.of(persona));
		mockMvc.perform(get("/api/v1/persona/maria")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].firstName").value("Ana"));
	}

	@Test
	void findOneReturns200() throws Exception {
		when(adapter.buscarPersona("MARIA", 123)).thenReturn(persona);
		mockMvc.perform(get("/api/v1/persona/MARIA/123")).andExpect(status().isOk())
				.andExpect(jsonPath("$.database").value("MariaDB"));
	}

	@Test
	void editReturns200() throws Exception {
		when(adapter.editarPersona(eq("MARIA"), eq(123), any(PersonaRequest.class))).thenReturn(persona);
		mockMvc.perform(put("/api/v1/persona/MARIA/123").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.status").value("OK"));
	}

	@Test
	void dropReturns200() throws Exception {
		when(adapter.eliminarPersona("MARIA", 123)).thenReturn(new Response("OK", "persona 123 eliminada de MariaDB", LocalDateTime.now()));
		mockMvc.perform(delete("/api/v1/persona/MARIA/123")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("OK"));
	}

	@Test
	void countReturns200() throws Exception {
		when(adapter.contarPersonas("MONGO")).thenReturn(new CountResponse("persona", "MongoDB", 5));
		mockMvc.perform(get("/api/v1/persona/MONGO/count")).andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(5));
	}

	@Test
	void getPhonesReturns200() throws Exception {
		when(adapter.telefonosPersona("MARIA", 123))
				.thenReturn(List.of(new TelefonoResponse("3001234567", "Claro", "123", "Ana Perez", "MariaDB", "OK")));
		mockMvc.perform(get("/api/v1/persona/MARIA/123/telefonos")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].number").value("3001234567"));
	}

	@Test
	void getStudiesReturns200() throws Exception {
		when(adapter.estudiosPersona("MARIA", 123)).thenReturn(List.of(
				new EstudioResponse("1", "123", "2020-06-30", "Javeriana", "Ingeniero", "Ana Perez", "MariaDB", "OK")));
		mockMvc.perform(get("/api/v1/persona/MARIA/123/estudios")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].professionName").value("Ingeniero"));
	}

	@Test
	void notFoundReturns404WithUniformBody() throws Exception {
		when(adapter.buscarPersona("MARIA", 1)).thenThrow(new NoExistException("The person with id 1 does not exist"));
		mockMvc.perform(get("/api/v1/persona/MARIA/1")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.mensaje").value("The person with id 1 does not exist"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	void invalidDatabaseReturns400() throws Exception {
		when(adapter.historial("ORACLE")).thenThrow(new InvalidOptionException("Invalid database option: ORACLE"));
		mockMvc.perform(get("/api/v1/persona/ORACLE")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void invalidDataReturns400() throws Exception {
		when(adapter.crearPersona(any(PersonaRequest.class))).thenThrow(new IllegalArgumentException("sex must be M or F: X"));
		mockMvc.perform(post("/api/v1/persona").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensaje").value("sex must be M or F: X"));
	}

	@Test
	void nonNumericIdReturns400() throws Exception {
		mockMvc.perform(get("/api/v1/persona/MARIA/abc")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void duplicatedReturns409() throws Exception {
		when(adapter.crearPersona(any(PersonaRequest.class))).thenThrow(new AlreadyExistsException("already exists"));
		mockMvc.perform(post("/api/v1/persona").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isConflict());
	}

	@Test
	void dropWithRelationsReturns409() throws Exception {
		when(adapter.eliminarPersona("MARIA", 123)).thenThrow(new DataIntegrityViolationException("has phones"));
		mockMvc.perform(delete("/api/v1/persona/MARIA/123")).andExpect(status().isConflict());
	}
}
