package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import org.springframework.dao.DataAccessException;

import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;

abstract class EntityCrudMenu {

	private static final int RETURN = 0;
	private static final int CREATE = 1;
	private static final int EDIT = 2;
	private static final int DELETE = 3;
	private static final int FIND = 4;
	private static final int LIST = 5;
	private static final int COUNT = 6;
	private String selectedDatabase;

	protected abstract String entityName();

	protected abstract void selectDatabase(String database) throws InvalidOptionException;

	protected abstract void create(Scanner keyboard) throws NoExistException;

	protected abstract void edit(Scanner keyboard) throws NoExistException;

	protected abstract void delete(Scanner keyboard) throws NoExistException;

	protected abstract void find(Scanner keyboard) throws NoExistException;

	protected abstract void list();

	protected abstract void count();

	/** Opciones adicionales de una entidad (por ejemplo, getPhones y getStudies en Persona). */
	protected void printExtraOptions() {
	}

	/** @return {@code true} si la opción adicional fue atendida. */
	protected boolean handleExtraOption(int option, Scanner keyboard) throws NoExistException {
		return false;
	}

	public final void iniciarMenu(Scanner keyboard) {
		while (true) {
			System.out.println("----------------------");
			System.out.println("Módulo de " + entityName());
			System.out.println("1 para MariaDB");
			System.out.println("2 para MongoDB");
			System.out.println("0 para regresar");
			int databaseOption = CliInput.readInteger(keyboard, "Seleccione la base de datos: ");
			if (databaseOption == RETURN) {
				return;
			}
			String database = databaseOption == 1 ? "MARIA" : databaseOption == 2 ? "MONGO" : null;
			if (database == null) {
				System.out.println("La opción elegida no es válida.");
				continue;
			}
			try {
				selectDatabase(database);
				selectedDatabase = database;
				menuOpciones(keyboard);
			} catch (InvalidOptionException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private void menuOpciones(Scanner keyboard) {
		while (true) {
			System.out.println("----------------------");
			System.out.println("Módulo de " + entityName() + " (" + selectedDatabase + ")");
			System.out.println(CREATE + " para crear");
			System.out.println(EDIT + " para editar");
			System.out.println(DELETE + " para eliminar");
			System.out.println(FIND + " para buscar por identificador");
			System.out.println(LIST + " para listar todos");
			System.out.println(COUNT + " para contar");
			printExtraOptions();
			System.out.println(RETURN + " para regresar");
			int option = CliInput.readInteger(keyboard, "Ingrese una opción: ");
			if (option == RETURN) {
				return;
			}
			try {
				switch (option) {
				case CREATE:
					create(keyboard);
					break;
				case EDIT:
					edit(keyboard);
					break;
				case DELETE:
					delete(keyboard);
					break;
				case FIND:
					find(keyboard);
					break;
				case LIST:
					list();
					break;
				case COUNT:
					count();
					break;
				default:
					if (!handleExtraOption(option, keyboard))
						System.out.println("La opción elegida no es válida.");
				}
			} catch (NoExistException | AlreadyExistsException | DataAccessException | IllegalArgumentException e) {
				System.out.println("No se pudo completar la operación: " + e.getMessage());
			}
		}
	}
}
