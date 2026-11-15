package co.edu.javeriana.as.personapp.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@ExtendWith(MockitoExtension.class)
class PersonaInputAdapterRestTest {

	@Mock
	private PersonOutputPort personOutputPortMaria;

	@Mock
	private PersonOutputPort personOutputPortMongo;

	private PersonaInputAdapterRest personaInputAdapterRest;

	@BeforeEach
	void setUp() {
		personaInputAdapterRest = new PersonaInputAdapterRest();
		ReflectionTestUtils.setField(personaInputAdapterRest, "personOutputPortMaria", personOutputPortMaria);
		ReflectionTestUtils.setField(personaInputAdapterRest, "personOutputPortMongo", personOutputPortMongo);
		ReflectionTestUtils.setField(personaInputAdapterRest, "personaMapperRest", new PersonaMapperRest());
		ReflectionTestUtils.setField(personaInputAdapterRest, "telefonoMapperRest", new TelefonoMapperRest());
		ReflectionTestUtils.setField(personaInputAdapterRest, "estudioMapperRest", new EstudioMapperRest());
	}

	@Test
	void crearPersonaRespondsWithChosenDatabase() throws Exception {
		when(personOutputPortMongo.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

		PersonaResponse response = personaInputAdapterRest
				.crearPersona(new PersonaRequest("123", "Ana", "Perez", "30", "F", "mongo"));

		assertEquals("MongoDB", response.getDatabase());
		assertEquals("OK", response.getStatus());
		verify(personOutputPortMaria, never()).save(any());
	}

	@Test
	void crearPersonaRejectsInvalidDatabase() {
		assertThrows(InvalidOptionException.class, () -> personaInputAdapterRest
				.crearPersona(new PersonaRequest("123", "Ana", "Perez", "30", "F", "ORACLE")));

		verifyNoInteractions(personOutputPortMaria, personOutputPortMongo);
	}

	@Test
	void crearPersonaRejectsInvalidData() {
		assertThrows(IllegalArgumentException.class, () -> personaInputAdapterRest
				.crearPersona(new PersonaRequest("123", "Ana", "Perez", "30", "X", "MARIA")));

		verifyNoInteractions(personOutputPortMaria, personOutputPortMongo);
	}

	@Test
	void editarPersonaTakesIdentificationFromPath() throws Exception {
		when(personOutputPortMaria.findById(123)).thenReturn(new Person(123, "Ana", "Perez", Gender.FEMALE));
		when(personOutputPortMaria.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

		PersonaResponse response = personaInputAdapterRest.editarPersona("maria", 123,
				new PersonaRequest(null, "Ana Maria", "Perez", "31", "F", null));

		assertEquals("123", response.getDni());
		assertEquals("Ana Maria", response.getFirstName());
		assertEquals("MariaDB", response.getDatabase());
	}

	@Test
	void buscarPersonaPropagatesNotFound() {
		assertThrows(NoExistException.class, () -> personaInputAdapterRest.buscarPersona("MONGO", 999));
		verify(personOutputPortMongo).findById(999);
	}

	@Test
	void telefonosPersonaUsesGetPhones() throws Exception {
		Person person = new Person(123, "Ana", "Perez", Gender.FEMALE);
		person.setPhoneNumbers(List.of(new Phone("3001234567", "Claro", person)));
		when(personOutputPortMaria.findById(123)).thenReturn(person);

		List<TelefonoResponse> phones = personaInputAdapterRest.telefonosPersona("MARIA", 123);

		assertEquals(1, phones.size());
		assertEquals("3001234567", phones.get(0).getNumber());
		assertEquals("Ana Perez", phones.get(0).getOwnerName());
	}

	@Test
	void contarPersonasCountsSelectedDatabase() throws Exception {
		when(personOutputPortMongo.find()).thenReturn(List.of(new Person(1, "A", "B", Gender.MALE)));

		assertEquals(1, personaInputAdapterRest.contarPersonas("mongo").getCount());
		verifyNoInteractions(personOutputPortMaria);
	}
}
