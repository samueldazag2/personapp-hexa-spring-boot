package co.edu.javeriana.as.personapp.mariadb.adapter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.PersonaMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;

@ExtendWith(MockitoExtension.class)
class PersonOutputAdapterMariaTest {

	@Mock
	private PersonaRepositoryMaria personaRepositoryMaria;
	@Mock
	private PersonaMapperMaria personaMapperMaria;
	@InjectMocks
	private PersonOutputAdapterMaria adapter;

	@Test
	void deleteRejectsPersonWithPhones() {
		PersonaEntity persona = new PersonaEntity(123);
		persona.setTelefonos(List.of(new TelefonoEntity("3001234567", "Claro")));
		when(personaRepositoryMaria.findById(123)).thenReturn(Optional.of(persona));

		assertThrows(DataIntegrityViolationException.class, () -> adapter.delete(123));
		verify(personaRepositoryMaria, never()).deleteById(any());
	}

	@Test
	void deleteRemovesPersonWithoutRelations() {
		when(personaRepositoryMaria.findById(123)).thenReturn(Optional.of(new PersonaEntity(123)), Optional.empty());

		assertTrue(adapter.delete(123));
		verify(personaRepositoryMaria).deleteById(123);
	}
}
