package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfessionInputAdapterCli;

@Component
public class ProfessionMenu extends EntityCrudMenu {

	@Autowired
	private ProfessionInputAdapterCli adapter;

	@Override
	protected String entityName() {
		return "Profesiones";
	}

	@Override
	protected void selectDatabase(String database) throws InvalidOptionException {
		adapter.setProfessionOutputPortInjection(database);
	}

	@Override
	protected void create(Scanner keyboard) {
		Profession profession = readProfession(keyboard, true);
		System.out.println("Profesión creada: " + adapter.crear(profession));
	}

	@Override
	protected void edit(Scanner keyboard) throws NoExistException {
		Integer identification = CliInput.readInteger(keyboard, "Identificador de la profesión: ");
		System.out.println("Profesión actualizada: " + adapter.editar(identification, readProfession(keyboard, false)));
	}

	@Override
	protected void delete(Scanner keyboard) throws NoExistException {
		Integer identification = CliInput.readInteger(keyboard, "Identificador de la profesión: ");
		System.out.println(adapter.eliminar(identification) ? "Profesión eliminada." : "No se pudo eliminar la profesión.");
	}

	@Override
	protected void find(Scanner keyboard) throws NoExistException {
		Integer identification = CliInput.readInteger(keyboard, "Identificador de la profesión: ");
		System.out.println(adapter.buscar(identification));
	}

	@Override
	protected void list() {
		adapter.listar().forEach(System.out::println);
	}

	@Override
	protected void count() {
		System.out.println("Total de profesiones: " + adapter.contar());
	}

	private Profession readProfession(Scanner keyboard, boolean includeIdentification) {
		Integer identification = includeIdentification
				? CliInput.readInteger(keyboard, "Identificador: ")
				: null;
		String name = CliInput.read(keyboard, "Nombre: ");
		String description = CliInput.read(keyboard, "Descripción (opcional): ");
		Profession profession = new Profession();
		if (identification != null) {
			profession.setIdentification(identification);
		}
		profession.setName(name);
		profession.setDescription(description.isEmpty() ? null : description);
		return profession;
	}
}
