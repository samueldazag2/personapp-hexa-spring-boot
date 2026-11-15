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

import co.edu.javeriana.as.personapp.adapter.EstudioInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.ErrorResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
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
@RequestMapping("/api/v1/estudio")
@Tag(name = "Estudio", description = "CRUD de estudios(id_prof, cc_per, fecha, univer) con llave compuesta en MARIA o MONGO")
public class EstudioControllerV1 {

	private static final String DB = "Base de datos: MARIA o MONGO (sin importar mayúsculas)";

	@Autowired
	private EstudioInputAdapterRest estudioInputAdapterRest;

	@Operation(summary = "findAll: lista todos los estudios")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado de estudios"),
			@ApiResponse(responseCode = "400", description = "Base de datos inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EstudioResponse> estudios(@Parameter(description = DB, example = "MARIA") @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into estudios REST API");
		return estudioInputAdapterRest.historial(database);
	}

	@Operation(summary = "count: cuenta los estudios")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Total de estudios"),
			@ApiResponse(responseCode = "400", description = "Base de datos inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public CountResponse contarEstudios(@Parameter(description = DB, example = "MARIA") @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into contarEstudios REST API");
		return estudioInputAdapterRest.contarEstudios(database);
	}

	@Operation(summary = "findOne: busca un estudio por llave compuesta (id_prof, cc_per)")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Estudio encontrado"),
			@ApiResponse(responseCode = "400", description = "Base de datos o llave inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "El estudio no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}/{idProf}/{ccPer}", produces = MediaType.APPLICATION_JSON_VALUE)
	public EstudioResponse buscarEstudio(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Id de la profesión", example = "1") @PathVariable Integer idProf,
			@Parameter(description = "Cédula de la persona", example = "123456789") @PathVariable Integer ccPer)
			throws InvalidOptionException, NoExistException {
		log.info("Into buscarEstudio REST API");
		return estudioInputAdapterRest.buscarEstudio(database, idProf, ccPer);
	}

	@Operation(summary = "create: crea un estudio en la base indicada en el body (persona y profesión deben existir)")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Estudio creado"),
			@ApiResponse(responseCode = "400", description = "Base de datos o datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "La persona o la profesión no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "El estudio ya existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public EstudioResponse crearEstudio(@RequestBody EstudioRequest request)
			throws InvalidOptionException, NoExistException {
		log.info("Into crearEstudio REST API");
		return estudioInputAdapterRest.crearEstudio(request);
	}

	@Operation(summary = "edit: edita fecha y universidad de un estudio (la llave se toma de la ruta)")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Estudio editado"),
			@ApiResponse(responseCode = "400", description = "Base de datos o datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "El estudio no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@PutMapping(path = "/{database}/{idProf}/{ccPer}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public EstudioResponse editarEstudio(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Id de la profesión", example = "1") @PathVariable Integer idProf,
			@Parameter(description = "Cédula de la persona", example = "123456789") @PathVariable Integer ccPer,
			@RequestBody EstudioRequest request) throws InvalidOptionException, NoExistException {
		log.info("Into editarEstudio REST API");
		return estudioInputAdapterRest.editarEstudio(database, idProf, ccPer, request);
	}

	@Operation(summary = "drop: elimina un estudio")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Estudio eliminado"),
			@ApiResponse(responseCode = "400", description = "Base de datos o llave inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "El estudio no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@DeleteMapping(path = "/{database}/{idProf}/{ccPer}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarEstudio(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Id de la profesión", example = "1") @PathVariable Integer idProf,
			@Parameter(description = "Cédula de la persona", example = "123456789") @PathVariable Integer ccPer)
			throws InvalidOptionException, NoExistException {
		log.info("Into eliminarEstudio REST API");
		return estudioInputAdapterRest.eliminarEstudio(database, idProf, ccPer);
	}
}
