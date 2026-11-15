package co.edu.javeriana.as.personapp.model.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado de la función drop")
public class Response {

	@Schema(example = "OK")
	private String status;
	@Schema(example = "persona 123456789 eliminada de MariaDB")
	private String description;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@Schema(type = "string", example = "15/11/2026")
	private LocalDateTime date;

	public Response(String status, String description, LocalDateTime date) {
		super();
		this.status = status;
		this.description = description;
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

}
