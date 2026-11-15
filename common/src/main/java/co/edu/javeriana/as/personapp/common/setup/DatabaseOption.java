package co.edu.javeriana.as.personapp.common.setup;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;

public enum DatabaseOption {
	MONGO, MARIA;

	/**
	 * Convierte el nombre de la base de datos recibido por un adaptador de entrada
	 * (sin importar mayúsculas) en la opción correspondiente.
	 */
	public static DatabaseOption fromString(String database) throws InvalidOptionException {
		for (DatabaseOption option : values()) {
			if (option.name().equalsIgnoreCase(database)) {
				return option;
			}
		}
		throw new InvalidOptionException("Invalid database option: " + database + " (use MARIA or MONGO)");
	}
}
