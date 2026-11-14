package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.adapter.PhoneInputAdapterCli;

@Component
public class PhoneMenu extends EntityCrudMenu {

	@Autowired
	private PhoneInputAdapterCli adapter;

	@Override
	protected String entityName() {
		return "Teléfonos";
	}

	@Override
	protected void selectDatabase(String database) throws InvalidOptionException {
		adapter.setPhoneOutputPortInjection(database);
	}

	@Override
	protected void create(Scanner keyboard) throws NoExistException {
		String number = CliInput.read(keyboard, "Número: ");
		System.out.println("Teléfono creado: " + adapter.crear(readPhone(keyboard, number)));
	}

	@Override
	protected void edit(Scanner keyboard) throws NoExistException {
		String number = CliInput.read(keyboard, "Número del teléfono a editar: ");
		System.out.println("Teléfono actualizado: " + adapter.editar(number, readPhone(keyboard, number)));
	}

	@Override
	protected void delete(Scanner keyboard) throws NoExistException {
		String number = CliInput.read(keyboard, "Número del teléfono: ");
		System.out.println(adapter.eliminar(number) ? "Teléfono eliminado." : "No se pudo eliminar el teléfono.");
	}

	@Override
	protected void find(Scanner keyboard) throws NoExistException {
		String number = CliInput.read(keyboard, "Número del teléfono: ");
		System.out.println(adapter.buscar(number));
	}

	@Override
	protected void list() {
		adapter.listar().forEach(System.out::println);
	}

	@Override
	protected void count() {
		System.out.println("Total de teléfonos: " + adapter.contar());
	}

	private Phone readPhone(Scanner keyboard, String number) throws NoExistException {
		String company = CliInput.read(keyboard, "Operador: ");
		Integer ownerId = CliInput.readInteger(keyboard, "Identificación del dueño: ");
		Person owner = adapter.buscarPersona(ownerId);
		return new Phone(number, company, owner);
	}
}
