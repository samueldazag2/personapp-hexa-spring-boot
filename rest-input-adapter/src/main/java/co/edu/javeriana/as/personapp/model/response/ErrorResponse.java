package co.edu.javeriana.as.personapp.model.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta uniforme de error")
public class ErrorResponse {
	@Schema(description = "Código HTTP", example = "404")
	private Integer status;
	@Schema(description = "Descripción del error", example = "The person with id 1 does not exist into db, cannot be found")
	private String mensaje;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(type = "string", example = "2026-11-15T15:00:00")
	private LocalDateTime timestamp;
}
