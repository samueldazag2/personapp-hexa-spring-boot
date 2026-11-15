package co.edu.javeriana.as.personapp.terminal.menu;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;

@ExtendWith(MockitoExtension.class)
class PersonaMenuTest {

	@Mock
	private PersonaInputAdapterCli adapter;
	@InjectMocks
	private PersonaMenu menu;

	private final ByteArrayOutputStream output = new ByteArrayOutputStream();
	private PrintStream originalOut;
	private final Person person = new Person(123, "Ana", "Perez", Gender.FEMALE);

	@BeforeEach
	void captureOutput() {
		originalOut = System.out;
		System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
	}

	@AfterEach
	void restoreOutput() {
		System.setOut(originalOut);
	}

	/** Base 1 (MariaDB), la opción y sus datos, 0 para regresar del menú de opciones y 0 para salir. */
	private void run(String lines) {
		menu.iniciarMenu(new Scanner(lines.replace(" ", "\n") + "\n0\n0\n"));
	}

	@Test
	void menuShowsSixCrudOptionsPlusPhonesAndStudies() {
		run("1");
		String text = output.toString(StandardCharsets.UTF_8);
		for (String option : List.of("1 para crear", "2 para editar", "3 para eliminar", "4 para buscar",
				"5 para listar", "6 para contar", "7 para ver los teléfonos", "8 para ver los estudios")) {
			assertTrue(text.contains(option), option);
		}
	}

	@Test
	void optionSevenShowsPhones() throws Exception {
		when(adapter.verTelefonos(123)).thenReturn(List.of(new Phone("3001234567", "Claro", person)));
		run("1 7 123");
		verify(adapter).verTelefonos(123);
		assertTrue(output.toString(StandardCharsets.UTF_8).contains("3001234567"));
	}

	@Test
	void optionEightShowsStudies() throws Exception {
		when(adapter.verEstudios(123)).thenReturn(List.of(new Study(person, new Profession(1, "Ingeniero"))));
		run("1 8 123");
		verify(adapter).verEstudios(123);
		assertTrue(output.toString(StandardCharsets.UTF_8).contains("Ingeniero"));
	}

	@Test
	void missingPersonDoesNotStopTheMenu() throws Exception {
		when(adapter.verTelefonos(999)).thenThrow(new NoExistException("The person with id 999 does not exist"));
		run("1 7 999");
		assertTrue(output.toString(StandardCharsets.UTF_8).contains("No se pudo completar la operación"));
	}

	@Test
	void duplicatedPersonDoesNotStopTheMenu() {
		when(adapter.crear(any(Person.class))).thenThrow(new AlreadyExistsException("already exists"));
		run("1 1 123 Ana Perez F 30");
		assertTrue(output.toString(StandardCharsets.UTF_8).contains("already exists"));
	}

	@Test
	void selectsTheChosenDatabase() throws Exception {
		run("2");
		verify(adapter).setPersonOutputPortInjection("MONGO");
	}
}
