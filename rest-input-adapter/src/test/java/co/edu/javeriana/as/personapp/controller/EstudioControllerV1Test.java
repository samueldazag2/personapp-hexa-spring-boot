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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import co.edu.javeriana.as.personapp.adapter.EstudioInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.Response;

@ExtendWith(MockitoExtension.class)
class EstudioControllerV1Test {

	private static final String JSON = "{\"professionId\":\"1\",\"personId\":\"123\",\"graduationDate\":\"2020-06-30\",\"universityName\":\"Javeriana\",\"database\":\"MONGO\"}";

	@Mock
	private EstudioInputAdapterRest adapter;
	@InjectMocks
	private EstudioControllerV1 controller;

	private MockMvc mockMvc;
	private final EstudioResponse estudio = new EstudioResponse("1", "123", "2020-06-30", "Javeriana", "Ingeniero",
			"Ana Perez", "MongoDB", "OK");

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new RestExceptionHandler()).build();
	}

	@Test
	void createReturns201() throws Exception {
		when(adapter.crearEstudio(any(EstudioRequest.class))).thenReturn(estudio);
		mockMvc.perform(post("/api/v1/estudio").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.universityName").value("Javeriana"));
	}

	@Test
	void findAllReturns200() throws Exception {
		when(adapter.historial("MONGO")).thenReturn(List.of(estudio));
		mockMvc.perform(get("/api/v1/estudio/MONGO")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].personId").value("123"));
	}

	@Test
	void findOneUsesCompositeKey() throws Exception {
		when(adapter.buscarEstudio("MONGO", 1, 123)).thenReturn(estudio);
		mockMvc.perform(get("/api/v1/estudio/MONGO/1/123")).andExpect(status().isOk())
				.andExpect(jsonPath("$.professionId").value("1"));
	}

	@Test
	void editReturns200() throws Exception {
		when(adapter.editarEstudio(eq("MONGO"), eq(1), eq(123), any(EstudioRequest.class))).thenReturn(estudio);
		mockMvc.perform(put("/api/v1/estudio/MONGO/1/123").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isOk());
	}

	@Test
	void dropReturns200() throws Exception {
		when(adapter.eliminarEstudio("MONGO", 1, 123))
				.thenReturn(new Response("OK", "estudio (1, 123) eliminado de MongoDB", LocalDateTime.now()));
		mockMvc.perform(delete("/api/v1/estudio/MONGO/1/123")).andExpect(status().isOk());
	}

	@Test
	void countReturns200() throws Exception {
		when(adapter.contarEstudios("MONGO")).thenReturn(new CountResponse("estudio", "MongoDB", 4));
		mockMvc.perform(get("/api/v1/estudio/MONGO/count")).andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(4));
	}

	@Test
	void notFoundReturns404() throws Exception {
		when(adapter.buscarEstudio("MONGO", 9, 9)).thenThrow(new NoExistException("The study does not exist"));
		mockMvc.perform(get("/api/v1/estudio/MONGO/9/9")).andExpect(status().isNotFound());
	}

	@Test
	void invalidDatabaseReturns400() throws Exception {
		when(adapter.historial("ORACLE")).thenThrow(new InvalidOptionException("Invalid database option: ORACLE"));
		mockMvc.perform(get("/api/v1/estudio/ORACLE")).andExpect(status().isBadRequest());
	}

	@Test
	void invalidDateReturns400() throws Exception {
		when(adapter.crearEstudio(any(EstudioRequest.class)))
				.thenThrow(new IllegalArgumentException("graduationDate must be a date with format yyyy-MM-dd: 30/06/2020"));
		mockMvc.perform(post("/api/v1/estudio").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isBadRequest());
	}
}
