package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Profesión guardada. database indica el motor: MariaDB o MongoDB")
public class ProfesionResponse extends ProfesionRequest {

	@Schema(example = "OK")
	private String status;

	public ProfesionResponse(String id, String name, String description, String database, String status) {
		super(id, name, description, database);
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
