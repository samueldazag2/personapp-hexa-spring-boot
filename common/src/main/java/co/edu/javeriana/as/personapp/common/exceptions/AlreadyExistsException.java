package co.edu.javeriana.as.personapp.common.exceptions;

/**
 * Se lanza al intentar crear un registro cuya llave ya existe. Es no verificada
 * para no cambiar la firma de los métodos {@code create} de los puertos de entrada.
 */
public class AlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AlreadyExistsException(String mensaje) {
		super(mensaje);
	}

}
