package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

final class CliInput {

	private CliInput() {
	}

	static String read(Scanner keyboard, String prompt) {
		System.out.print(prompt);
		return keyboard.nextLine().trim();
	}

	static int readInteger(Scanner keyboard, String prompt) {
		while (true) {
			String value = read(keyboard, prompt);
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				System.out.println("Ingrese un número entero válido.");
			}
		}
	}

	static Integer readOptionalInteger(Scanner keyboard, String prompt) {
		while (true) {
			String value = read(keyboard, prompt);
			if (value.isEmpty()) {
				return null;
			}
			try {
				return Integer.valueOf(value);
			} catch (NumberFormatException e) {
				System.out.println("Ingrese un número entero válido o deje el campo vacío.");
			}
		}
	}

	static LocalDate readOptionalDate(Scanner keyboard, String prompt) {
		while (true) {
			String value = read(keyboard, prompt);
			if (value.isEmpty()) {
				return null;
			}
			try {
				return LocalDate.parse(value);
			} catch (DateTimeParseException e) {
				System.out.println("Ingrese una fecha válida con formato AAAA-MM-DD o deje el campo vacío.");
			}
		}
	}
}
