package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Persona guardada. database indica el motor: MariaDB o MongoDB")
public class PersonaResponse extends PersonaRequest {

	@Schema(example = "OK")
	private String status;

	public PersonaResponse(String dni, String firstName, String lastName, String age, String sex, String database, String status) {
		super(dni, firstName, lastName, age, sex, database);
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
