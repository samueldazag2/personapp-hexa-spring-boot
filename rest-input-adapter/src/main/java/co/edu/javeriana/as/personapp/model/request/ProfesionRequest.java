package co.edu.javeriana.as.personapp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de una profesión")
public class ProfesionRequest {
	@Schema(description = "Identificador (id). En PUT se toma de la ruta", example = "10")
	private String id;
	@Schema(description = "Nombre (nom)", example = "Arquitecto")
	private String name;
	@Schema(description = "Descripción (des, opcional)", example = "Diseño de edificaciones")
	private String description;
	@Schema(description = "Base de datos: MARIA o MONGO (solo en POST)", example = "MARIA")
	private String database;
}
