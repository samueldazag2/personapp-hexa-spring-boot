package co.edu.javeriana.as.personapp.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Resultado de la función count")
public class CountResponse {
	@Schema(example = "persona")
	private String entity;
	@Schema(example = "MariaDB")
	private String database;
	@Schema(example = "5")
	private Integer count;
}
