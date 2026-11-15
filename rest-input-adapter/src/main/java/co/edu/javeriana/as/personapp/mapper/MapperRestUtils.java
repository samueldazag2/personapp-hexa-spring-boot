package co.edu.javeriana.as.personapp.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;

/**
 * Validaciones y conversiones compartidas por los mappers REST. Cualquier dato inválido se reporta
 * con {@link IllegalArgumentException}, que el manejador de errores traduce a HTTP 400.
 */
public final class MapperRestUtils {

	private MapperRestUtils() {
	}

	public static String databaseName(DatabaseOption database) {
		return database == DatabaseOption.MARIA ? "MariaDB" : "MongoDB";
	}

	static String required(String value, String field) {
		if (value == null || value.isBlank())
			throw new IllegalArgumentException(field + " is required");
		return value.trim();
	}

	static Integer requiredInteger(String value, String field) {
		return parseInteger(required(value, field), field);
	}

	static Integer optionalInteger(String value, String field) {
		return value == null || value.isBlank() ? null : parseInteger(value, field);
	}

	static String optional(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}

	static LocalDate optionalDate(String value, String field) {
		if (value == null || value.isBlank())
			return null;
		try {
			return LocalDate.parse(value.trim());
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(field + " must be a date with format yyyy-MM-dd: " + value);
		}
	}

	static String text(Object value) {
		return value != null ? value.toString() : "";
	}

	private static Integer parseInteger(String value, String field) {
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(field + " must be a number: " + value);
		}
	}
}
