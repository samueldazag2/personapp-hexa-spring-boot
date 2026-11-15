package co.edu.javeriana.as.personapp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de una persona")
public class PersonaRequest {
	@Schema(description = "Cédula (cc). En PUT se toma de la ruta", example = "111222333")
	private String dni;
	@Schema(description = "Nombre", example = "Laura")
	private String firstName;
	@Schema(description = "Apellido", example = "Gomez")
	private String lastName;
	@Schema(description = "Edad (opcional)", example = "25")
	private String age;
	@Schema(description = "Género: M o F", example = "F", allowableValues = { "M", "F" })
	private String sex;
	@Schema(description = "Base de datos: MARIA o MONGO (solo en POST)", example = "MARIA")
	private String database;
}
