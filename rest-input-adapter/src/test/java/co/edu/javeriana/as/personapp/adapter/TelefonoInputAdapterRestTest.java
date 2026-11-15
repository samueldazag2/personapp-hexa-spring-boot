package co.edu.javeriana.as.personapp.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@ExtendWith(MockitoExtension.class)
class TelefonoInputAdapterRestTest {

	@Mock
	private PhoneOutputPort phoneOutputPortMaria;
	@Mock
	private PhoneOutputPort phoneOutputPortMongo;
	@Mock
	private PersonOutputPort personOutputPortMaria;
	@Mock
	private PersonOutputPort personOutputPortMongo;

	private TelefonoInputAdapterRest adapter;

	@BeforeEach
	void setUp() {
		adapter = new TelefonoInputAdapterRest();
		ReflectionTestUtils.setField(adapter, "phoneOutputPortMaria", phoneOutputPortMaria);
		ReflectionTestUtils.setField(adapter, "phoneOutputPortMongo", phoneOutputPortMongo);
		ReflectionTestUtils.setField(adapter, "personOutputPortMaria", personOutputPortMaria);
		ReflectionTestUtils.setField(adapter, "personOutputPortMongo", personOutputPortMongo);
		ReflectionTestUtils.setField(adapter, "telefonoMapperRest", new TelefonoMapperRest());
	}

	@Test
	void crearTelefonoUsesExistingOwnerOfSameDatabase() throws Exception {
		Person owner = new Person(123, "Ana", "Perez", Gender.FEMALE);
		when(personOutputPortMongo.findById(123)).thenReturn(owner);
		when(phoneOutputPortMongo.save(any(Phone.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TelefonoResponse response = adapter.crearTelefono(new TelefonoRequest("3001234567", "Claro", "123", "MONGO"));

		assertEquals("Ana Perez", response.getOwnerName());
		assertEquals("MongoDB", response.getDatabase());
		verifyNoInteractions(phoneOutputPortMaria, personOutputPortMaria);
	}

	@Test
	void crearTelefonoRejectsMissingOwner() {
		assertThrows(NoExistException.class,
				() -> adapter.crearTelefono(new TelefonoRequest("3001234567", "Claro", "999", "MARIA")));
		verify(phoneOutputPortMaria, never()).save(any());
	}

	@Test
	void editarTelefonoRejectsMissingPhone() {
		when(personOutputPortMaria.findById(123)).thenReturn(new Person(123, "Ana", "Perez", Gender.FEMALE));

		assertThrows(NoExistException.class,
				() -> adapter.editarTelefono("MARIA", "3000000000", new TelefonoRequest(null, "Tigo", "123", null)));
		verify(phoneOutputPortMaria, never()).save(any());
	}
}
