package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;

@Component
public class PersonaMenu extends EntityCrudMenu {

	@Autowired
	private PersonaInputAdapterCli adapter;

	@Override
	protected String entityName() {
		return "Personas";
	}

	@Override
	protected void selectDatabase(String database) throws InvalidOptionException {
		adapter.setPersonOutputPortInjection(database);
	}

	@Override
	protected void create(Scanner keyboard) {
		Person person = readPerson(keyboard, true);
		System.out.println("Persona creada: " + adapter.crear(person));
	}

	@Override
	protected void edit(Scanner keyboard) throws NoExistException {
		Integer identification = CliInput.readInteger(keyboard, "Identificación de la persona: ");
		Person person = readPerson(keyboard, false);
		System.out.println("Persona actualizada: " + adapter.editar(identification, person));
	}

	@Override
	protected void delete(Scanner keyboard) throws NoExistException {
		Integer identification = CliInput.readInteger(keyboard, "Identificación de la persona: ");
		System.out.println(adapter.eliminar(identification) ? "Persona eliminada." : "No se pudo eliminar la persona.");
	}

	@Override
	protected void find(Scanner keyboard) throws NoExistException {
		Integer identification = CliInput.readInteger(keyboard, "Identificación de la persona: ");
		System.out.println(adapter.buscar(identification));
	}

	@Override
	protected void list() {
		adapter.historial();
	}

	@Override
	protected void count() {
		System.out.println("Total de personas: " + adapter.contar());
	}

	private Person readPerson(Scanner keyboard, boolean includeIdentification) {
		Integer identification = includeIdentification
				? CliInput.readInteger(keyboard, "Identificación: ")
				: null;
		String firstName = CliInput.read(keyboard, "Nombre: ");
		String lastName = CliInput.read(keyboard, "Apellido: ");
		Gender gender = readGender(keyboard);
		Integer age = CliInput.readOptionalInteger(keyboard, "Edad (opcional): ");
		Person person = new Person();
		if (identification != null) {
			person.setIdentification(identification);
		}
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setGender(gender);
		person.setAge(age);
		return person;
	}

	private Gender readGender(Scanner keyboard) {
		while (true) {
			String gender = CliInput.read(keyboard, "Género (M/F): ");
			if ("M".equalsIgnoreCase(gender) || "MALE".equalsIgnoreCase(gender)) {
				return Gender.MALE;
			}
			if ("F".equalsIgnoreCase(gender) || "FEMALE".equalsIgnoreCase(gender)) {
				return Gender.FEMALE;
			}
			System.out.println("Ingrese M (masculino) o F (femenino).");
		}
	}
}
