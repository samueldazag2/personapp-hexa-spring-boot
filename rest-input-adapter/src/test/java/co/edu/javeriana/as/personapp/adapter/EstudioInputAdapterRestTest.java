package co.edu.javeriana.as.personapp.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;

@ExtendWith(MockitoExtension.class)
class EstudioInputAdapterRestTest {

	@Mock
	private StudyOutputPort studyOutputPortMaria;
	@Mock
	private StudyOutputPort studyOutputPortMongo;
	@Mock
	private PersonOutputPort personOutputPortMaria;
	@Mock
	private PersonOutputPort personOutputPortMongo;
	@Mock
	private ProfessionOutputPort professionOutputPortMaria;
	@Mock
	private ProfessionOutputPort professionOutputPortMongo;

	private EstudioInputAdapterRest adapter;

	private final Person person = new Person(123, "Ana", "Perez", Gender.FEMALE);
	private final Profession profession = new Profession(1, "Ingeniero");

	@BeforeEach
	void setUp() {
		adapter = new EstudioInputAdapterRest();
		ReflectionTestUtils.setField(adapter, "studyOutputPortMaria", studyOutputPortMaria);
		ReflectionTestUtils.setField(adapter, "studyOutputPortMongo", studyOutputPortMongo);
		ReflectionTestUtils.setField(adapter, "personOutputPortMaria", personOutputPortMaria);
		ReflectionTestUtils.setField(adapter, "personOutputPortMongo", personOutputPortMongo);
		ReflectionTestUtils.setField(adapter, "professionOutputPortMaria", professionOutputPortMaria);
		ReflectionTestUtils.setField(adapter, "professionOutputPortMongo", professionOutputPortMongo);
		ReflectionTestUtils.setField(adapter, "estudioMapperRest", new EstudioMapperRest());
	}

	@Test
	void crearEstudioResolvesPersonAndProfession() throws Exception {
		when(personOutputPortMaria.findById(123)).thenReturn(person);
		when(professionOutputPortMaria.findById(1)).thenReturn(profession);
		when(studyOutputPortMaria.save(any(Study.class))).thenAnswer(invocation -> invocation.getArgument(0));

		EstudioResponse response = adapter
				.crearEstudio(new EstudioRequest("1", "123", "2020-06-30", "Javeriana", "maria"));

		assertEquals("Ingeniero", response.getProfessionName());
		assertEquals("Ana Perez", response.getPersonName());
		assertEquals("2020-06-30", response.getGraduationDate());
	}

	@Test
	void crearEstudioRejectsMissingProfession() {
		when(personOutputPortMongo.findById(123)).thenReturn(person);

		assertThrows(NoExistException.class,
				() -> adapter.crearEstudio(new EstudioRequest("9", "123", null, null, "MONGO")));
		verify(studyOutputPortMongo, never()).save(any());
	}

	@Test
	void editarEstudioKeepsCompositeKeyFromPath() throws Exception {
		when(studyOutputPortMaria.findById(1, 123)).thenReturn(new Study(person, profession));
		when(studyOutputPortMaria.save(any(Study.class))).thenAnswer(invocation -> invocation.getArgument(0));

		EstudioResponse response = adapter.editarEstudio("MARIA", 1, 123,
				new EstudioRequest(null, null, "2021-01-15", "Andes", null));

		assertEquals("1", response.getProfessionId());
		assertEquals("123", response.getPersonId());
		assertEquals(LocalDate.of(2021, 1, 15).toString(), response.getGraduationDate());
		assertEquals("Andes", response.getUniversityName());
	}
}
