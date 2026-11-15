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

import co.edu.javeriana.as.personapp.adapter.ProfesionInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;

@ExtendWith(MockitoExtension.class)
class ProfesionControllerV1Test {

	private static final String JSON = "{\"id\":\"1\",\"name\":\"Ingeniero\",\"description\":\"Sistemas\",\"database\":\"MONGO\"}";

	@Mock
	private ProfesionInputAdapterRest adapter;
	@InjectMocks
	private ProfesionControllerV1 controller;

	private MockMvc mockMvc;
	private final ProfesionResponse profesion = new ProfesionResponse("1", "Ingeniero", "Sistemas", "MongoDB", "OK");

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new RestExceptionHandler()).build();
	}

	@Test
	void createReturns201() throws Exception {
		when(adapter.crearProfesion(any(ProfesionRequest.class))).thenReturn(profesion);
		mockMvc.perform(post("/api/v1/profesion").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Ingeniero"));
	}

	@Test
	void findAllReturns200() throws Exception {
		when(adapter.historial("MONGO")).thenReturn(List.of(profesion));
		mockMvc.perform(get("/api/v1/profesion/MONGO")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value("1"));
	}

	@Test
	void findOneReturns200() throws Exception {
		when(adapter.buscarProfesion("MONGO", 1)).thenReturn(profesion);
		mockMvc.perform(get("/api/v1/profesion/MONGO/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value("Sistemas"));
	}

	@Test
	void editReturns200() throws Exception {
		when(adapter.editarProfesion(eq("MONGO"), eq(1), any(ProfesionRequest.class))).thenReturn(profesion);
		mockMvc.perform(put("/api/v1/profesion/MONGO/1").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isOk());
	}

	@Test
	void dropReturns200() throws Exception {
		when(adapter.eliminarProfesion("MONGO", 1)).thenReturn(new Response("OK", "profesion 1 eliminada de MongoDB", LocalDateTime.now()));
		mockMvc.perform(delete("/api/v1/profesion/MONGO/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("OK"));
	}

	@Test
	void countReturns200() throws Exception {
		when(adapter.contarProfesiones("MONGO")).thenReturn(new CountResponse("profesion", "MongoDB", 4));
		mockMvc.perform(get("/api/v1/profesion/MONGO/count")).andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(4));
	}

	@Test
	void notFoundReturns404() throws Exception {
		when(adapter.eliminarProfesion("MONGO", 99)).thenThrow(new NoExistException("The profession with id 99 does not exist"));
		mockMvc.perform(delete("/api/v1/profesion/MONGO/99")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void invalidDatabaseReturns400() throws Exception {
		when(adapter.contarProfesiones("SQL")).thenThrow(new InvalidOptionException("Invalid database option: SQL"));
		mockMvc.perform(get("/api/v1/profesion/SQL/count")).andExpect(status().isBadRequest());
	}

	@Test
	void malformedBodyReturns400() throws Exception {
		mockMvc.perform(post("/api/v1/profesion").contentType(MediaType.APPLICATION_JSON).content("{bad json"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
	}
}
