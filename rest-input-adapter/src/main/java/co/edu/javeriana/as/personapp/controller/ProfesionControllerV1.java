package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.ProfesionInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.ErrorResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/profesion")
@Tag(name = "Profesion", description = "CRUD de profesion(id, nom, des) en MARIA o MONGO")
public class ProfesionControllerV1 {

	private static final String DB = "Base de datos: MARIA o MONGO (sin importar mayúsculas)";

	@Autowired
	private ProfesionInputAdapterRest profesionInputAdapterRest;

	@Operation(summary = "findAll: lista todas las profesiones")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado de profesiones"),
			@ApiResponse(responseCode = "400", description = "Base de datos inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProfesionResponse> profesiones(@Parameter(description = DB, example = "MARIA") @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into profesiones REST API");
		return profesionInputAdapterRest.historial(database);
	}

	@Operation(summary = "count: cuenta las profesiones")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Total de profesiones"),
			@ApiResponse(responseCode = "400", description = "Base de datos inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public CountResponse contarProfesiones(@Parameter(description = DB, example = "MARIA") @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into contarProfesiones REST API");
		return profesionInputAdapterRest.contarProfesiones(database);
	}

	@Operation(summary = "findOne: busca una profesión por id")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Profesión encontrada"),
			@ApiResponse(responseCode = "400", description = "Base de datos o id inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "La profesión no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ProfesionResponse buscarProfesion(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Id de la profesión", example = "1") @PathVariable Integer id)
			throws InvalidOptionException, NoExistException {
		log.info("Into buscarProfesion REST API");
		return profesionInputAdapterRest.buscarProfesion(database, id);
	}

	@Operation(summary = "create: crea una profesión en la base indicada en el body")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Profesión creada"),
			@ApiResponse(responseCode = "400", description = "Base de datos o datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "El id ya existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ProfesionResponse crearProfesion(@RequestBody ProfesionRequest request) throws InvalidOptionException {
		log.info("Into crearProfesion REST API");
		return profesionInputAdapterRest.crearProfesion(request);
	}

	@Operation(summary = "edit: edita una profesión (el id se toma de la ruta)")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Profesión editada"),
			@ApiResponse(responseCode = "400", description = "Base de datos o datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "La profesión no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@PutMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ProfesionResponse editarProfesion(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Id de la profesión", example = "1") @PathVariable Integer id,
			@RequestBody ProfesionRequest request) throws InvalidOptionException, NoExistException {
		log.info("Into editarProfesion REST API");
		return profesionInputAdapterRest.editarProfesion(database, id, request);
	}

	@Operation(summary = "drop: elimina una profesión (sin estudios asociados)")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Profesión eliminada"),
			@ApiResponse(responseCode = "400", description = "Base de datos o id inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "La profesión no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "La profesión tiene estudios", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@DeleteMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarProfesion(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Id de la profesión", example = "1") @PathVariable Integer id)
			throws InvalidOptionException, NoExistException {
		log.info("Into eliminarProfesion REST API");
		return profesionInputAdapterRest.eliminarProfesion(database, id);
	}
}
