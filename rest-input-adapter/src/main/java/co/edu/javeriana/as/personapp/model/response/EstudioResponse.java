package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estudio guardado. database indica el motor: MariaDB o MongoDB")
public class EstudioResponse extends EstudioRequest {

	@Schema(description = "Nombre de la profesión", example = "Medico")
	private String professionName;
	@Schema(description = "Nombre completo de la persona", example = "Pepito Perez")
	private String personName;
	@Schema(example = "OK")
	private String status;

	public EstudioResponse(String professionId, String personId, String graduationDate, String universityName,
			String professionName, String personName, String database, String status) {
		super(professionId, personId, graduationDate, universityName, database);
		this.professionName = professionName;
		this.personName = personName;
		this.status = status;
	}

	public String getProfessionName() {
		return professionName;
	}

	public void setProfessionName(String professionName) {
		this.professionName = professionName;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
