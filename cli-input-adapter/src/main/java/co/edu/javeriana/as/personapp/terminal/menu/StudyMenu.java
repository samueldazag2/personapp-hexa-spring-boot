package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.adapter.StudyInputAdapterCli;

@Component
public class StudyMenu extends EntityCrudMenu {

	@Autowired
	private StudyInputAdapterCli adapter;

	@Override
	protected String entityName() {
		return "Estudios";
	}

	@Override
	protected void selectDatabase(String database) throws InvalidOptionException {
		adapter.setStudyOutputPortInjection(database);
	}

	@Override
	protected void create(Scanner keyboard) throws NoExistException {
		System.out.println("Estudio creado: " + adapter.crear(readStudy(keyboard)));
	}

	@Override
	protected void edit(Scanner keyboard) throws NoExistException {
		Integer professionId = CliInput.readInteger(keyboard, "Identificador de la profesión del estudio: ");
		Integer personId = CliInput.readInteger(keyboard, "Identificación de la persona del estudio: ");
		Study changes = readStudyDetails(keyboard,
				adapter.buscarPersona(personId),
				adapter.buscarProfesion(professionId));
		System.out.println("Estudio actualizado: " + adapter.editar(professionId, personId, changes));
	}

	@Override
	protected void delete(Scanner keyboard) throws NoExistException {
		Integer professionId = CliInput.readInteger(keyboard, "Identificador de la profesión: ");
		Integer personId = CliInput.readInteger(keyboard, "Identificación de la persona: ");
		System.out.println(adapter.eliminar(professionId, personId)
				? "Estudio eliminado."
				: "No se pudo eliminar el estudio.");
	}

	@Override
	protected void find(Scanner keyboard) throws NoExistException {
		Integer professionId = CliInput.readInteger(keyboard, "Identificador de la profesión: ");
		Integer personId = CliInput.readInteger(keyboard, "Identificación de la persona: ");
		System.out.println(adapter.buscar(professionId, personId));
	}

	@Override
	protected void list() {
		adapter.listar().forEach(System.out::println);
	}

	@Override
	protected void count() {
		System.out.println("Total de estudios: " + adapter.contar());
	}

	private Study readStudy(Scanner keyboard) throws NoExistException {
		Integer personId = CliInput.readInteger(keyboard, "Identificación de la persona: ");
		Integer professionId = CliInput.readInteger(keyboard, "Identificador de la profesión: ");
		Person person = adapter.buscarPersona(personId);
		Profession profession = adapter.buscarProfesion(professionId);
		return readStudyDetails(keyboard, person, profession);
	}

	private Study readStudyDetails(Scanner keyboard, Person person, Profession profession) {
		LocalDate graduationDate = CliInput.readOptionalDate(keyboard, "Fecha de grado (AAAA-MM-DD, opcional): ");
		String university = CliInput.read(keyboard, "Universidad (opcional): ");
		Study study = new Study(person, profession);
		study.setGraduationDate(graduationDate);
		study.setUniversityName(university.isEmpty() ? null : university);
		return study;
	}
}
