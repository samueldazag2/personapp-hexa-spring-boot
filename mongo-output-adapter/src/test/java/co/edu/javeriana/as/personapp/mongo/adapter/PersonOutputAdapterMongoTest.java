package co.edu.javeriana.as.personapp.mongo.adapter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.PersonaMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.PersonaRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.TelefonoRepositoryMongo;

@ExtendWith(MockitoExtension.class)
class PersonOutputAdapterMongoTest {

	@Mock
	private PersonaRepositoryMongo personaRepositoryMongo;
	@Mock
	private PersonaMapperMongo personaMapperMongo;
	@Mock
	private EstudiosRepositoryMongo studies;
	@Mock
	private TelefonoRepositoryMongo phones;
	@InjectMocks
	private PersonOutputAdapterMongo adapter;

	@Test
	void saveDoesNotSwallowWriteErrors() {
		Person person = new Person(123, "Ana", "Perez", Gender.FEMALE);
		PersonaDocument document = new PersonaDocument();
		when(personaMapperMongo.fromDomainToAdapter(person)).thenReturn(document);
		when(personaRepositoryMongo.save(document)).thenThrow(new DuplicateKeyException("duplicate"));

		assertThrows(DuplicateKeyException.class, () -> adapter.save(person));
	}

	@Test
	void deleteRejectsPersonWithPhones() {
		when(phones.existsByOwnerId(123)).thenReturn(true);

		assertThrows(DataIntegrityViolationException.class, () -> adapter.delete(123));
		verify(personaRepositoryMongo, never()).deleteById(any());
	}
}
