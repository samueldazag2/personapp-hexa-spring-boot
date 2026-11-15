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

import co.edu.javeriana.as.personapp.adapter.TelefonoInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@ExtendWith(MockitoExtension.class)
class TelefonoControllerV1Test {

	private static final String JSON = "{\"number\":\"3001234567\",\"company\":\"Claro\",\"ownerId\":\"123\",\"database\":\"MARIA\"}";

	@Mock
	private TelefonoInputAdapterRest adapter;
	@InjectMocks
	private TelefonoControllerV1 controller;

	private MockMvc mockMvc;
	private final TelefonoResponse telefono = new TelefonoResponse("3001234567", "Claro", "123", "Ana Perez", "MariaDB", "OK");

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new RestExceptionHandler()).build();
	}

	@Test
	void createReturns201() throws Exception {
		when(adapter.crearTelefono(any(TelefonoRequest.class))).thenReturn(telefono);
		mockMvc.perform(post("/api/v1/telefono").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.ownerName").value("Ana Perez"));
	}

	@Test
	void findAllReturns200() throws Exception {
		when(adapter.historial("MARIA")).thenReturn(List.of(telefono));
		mockMvc.perform(get("/api/v1/telefono/MARIA")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].company").value("Claro"));
	}

	@Test
	void findOneReturns200() throws Exception {
		when(adapter.buscarTelefono("MARIA", "3001234567")).thenReturn(telefono);
		mockMvc.perform(get("/api/v1/telefono/MARIA/3001234567")).andExpect(status().isOk())
				.andExpect(jsonPath("$.number").value("3001234567"));
	}

	@Test
	void editReturns200() throws Exception {
		when(adapter.editarTelefono(eq("MARIA"), eq("3001234567"), any(TelefonoRequest.class))).thenReturn(telefono);
		mockMvc.perform(put("/api/v1/telefono/MARIA/3001234567").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isOk());
	}

	@Test
	void dropReturns200() throws Exception {
		when(adapter.eliminarTelefono("MARIA", "3001234567"))
				.thenReturn(new Response("OK", "telefono 3001234567 eliminado de MariaDB", LocalDateTime.now()));
		mockMvc.perform(delete("/api/v1/telefono/MARIA/3001234567")).andExpect(status().isOk());
	}

	@Test
	void countReturns200() throws Exception {
		when(adapter.contarTelefonos("MARIA")).thenReturn(new CountResponse("telefono", "MariaDB", 5));
		mockMvc.perform(get("/api/v1/telefono/MARIA/count")).andExpect(status().isOk())
				.andExpect(jsonPath("$.entity").value("telefono"));
	}

	@Test
	void missingOwnerReturns404() throws Exception {
		when(adapter.crearTelefono(any(TelefonoRequest.class))).thenThrow(new NoExistException("The person with id 123 does not exist"));
		mockMvc.perform(post("/api/v1/telefono").contentType(MediaType.APPLICATION_JSON).content(JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void invalidDatabaseReturns400() throws Exception {
		when(adapter.buscarTelefono("X", "1")).thenThrow(new InvalidOptionException("Invalid database option: X"));
		mockMvc.perform(get("/api/v1/telefono/X/1")).andExpect(status().isBadRequest());
	}
}
