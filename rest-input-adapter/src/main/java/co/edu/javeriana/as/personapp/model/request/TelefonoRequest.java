package co.edu.javeriana.as.personapp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de un teléfono")
public class TelefonoRequest {
	@Schema(description = "Número (num). En PUT se toma de la ruta", example = "3109998877")
	private String number;
	@Schema(description = "Operador (oper)", example = "Tigo")
	private String company;
	@Schema(description = "Cédula del dueño (duenio -> persona.cc)", example = "123456789")
	private String ownerId;
	@Schema(description = "Base de datos: MARIA o MONGO (solo en POST)", example = "MARIA")
	private String database;
}
