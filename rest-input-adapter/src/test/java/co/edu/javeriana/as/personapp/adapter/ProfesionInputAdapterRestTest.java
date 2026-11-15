package co.edu.javeriana.as.personapp.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mapper.ProfesionMapperRest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;

@ExtendWith(MockitoExtension.class)
class ProfesionInputAdapterRestTest {

	@Mock
	private ProfessionOutputPort professionOutputPortMaria;
	@Mock
	private ProfessionOutputPort professionOutputPortMongo;

	private ProfesionInputAdapterRest adapter;

	@BeforeEach
	void setUp() {
		adapter = new ProfesionInputAdapterRest();
		ReflectionTestUtils.setField(adapter, "professionOutputPortMaria", professionOutputPortMaria);
		ReflectionTestUtils.setField(adapter, "professionOutputPortMongo", professionOutputPortMongo);
		ReflectionTestUtils.setField(adapter, "profesionMapperRest", new ProfesionMapperRest());
	}

	@Test
	void historialUsesSelectedDatabase() throws Exception {
		when(professionOutputPortMongo.find()).thenReturn(List.of(new Profession(1, "Ingeniero")));

		List<ProfesionResponse> response = adapter.historial("mongo");

		assertEquals(1, response.size());
		assertEquals("MongoDB", response.get(0).getDatabase());
		verifyNoInteractions(professionOutputPortMaria);
	}

	@Test
	void crearProfesionRejectsDuplicatedId() {
		when(professionOutputPortMaria.findById(1)).thenReturn(new Profession(1, "Ingeniero"));

		assertThrows(AlreadyExistsException.class,
				() -> adapter.crearProfesion(new ProfesionRequest("1", "Otra", null, "MARIA")));
	}

	@Test
	void historialRejectsInvalidDatabase() {
		assertThrows(InvalidOptionException.class, () -> adapter.historial("postgres"));
		verifyNoInteractions(professionOutputPortMaria, professionOutputPortMongo);
	}

	@Test
	void editarProfesionTakesIdFromPath() throws Exception {
		when(professionOutputPortMaria.findById(1)).thenReturn(new Profession(1, "Ingeniero"));
		when(professionOutputPortMaria.save(any(Profession.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ProfesionResponse response = adapter.editarProfesion("MARIA", 1, new ProfesionRequest(null, "Ing. Sistemas", "", null));

		assertEquals("1", response.getId());
		assertEquals("Ing. Sistemas", response.getName());
	}
}
