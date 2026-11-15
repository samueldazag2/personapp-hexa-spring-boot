package co.edu.javeriana.as.personapp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de un estudio (llave compuesta id_prof + cc_per)")
public class EstudioRequest {
	@Schema(description = "Id de la profesión (id_prof). En PUT se toma de la ruta", example = "2")
	private String professionId;
	@Schema(description = "Cédula de la persona (cc_per). En PUT se toma de la ruta", example = "987654321")
	private String personId;
	@Schema(description = "Fecha de grado (fecha, AAAA-MM-DD, opcional)", example = "2020-06-30")
	private String graduationDate;
	@Schema(description = "Universidad (univer, opcional)", example = "Universidad Nacional")
	private String universityName;
	@Schema(description = "Base de datos: MARIA o MONGO (solo en POST)", example = "MARIA")
	private String database;
}
