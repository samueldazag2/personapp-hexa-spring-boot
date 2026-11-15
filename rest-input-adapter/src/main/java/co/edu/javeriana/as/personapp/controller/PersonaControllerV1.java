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

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.ErrorResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
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
@RequestMapping("/api/v1/persona")
@Tag(name = "Persona", description = "CRUD de persona(cc, nombre, apellido, genero M/F, edad) en MARIA o MONGO")
public class PersonaControllerV1 {

	private static final String DB = "Base de datos: MARIA o MONGO (sin importar mayúsculas)";

	@Autowired
	private PersonaInputAdapterRest personaInputAdapterRest;

	@Operation(summary = "findAll: lista todas las personas")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado de personas"),
			@ApiResponse(responseCode = "400", description = "Base de datos inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonaResponse> personas(@Parameter(description = DB, example = "MARIA") @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into personas REST API");
		return personaInputAdapterRest.historial(database);
	}

	@Operation(summary = "count: cuenta las personas")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Total de personas"),
			@ApiResponse(responseCode = "400", description = "Base de datos inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public CountResponse contarPersonas(@Parameter(description = DB, example = "MARIA") @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into contarPersonas REST API");
		return personaInputAdapterRest.contarPersonas(database);
	}

	@Operation(summary = "findOne: busca una persona por cédula")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Persona encontrada"),
			@ApiResponse(responseCode = "400", description = "Base de datos o cédula inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "La persona no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse buscarPersona(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Cédula", example = "123456789") @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		log.info("Into buscarPersona REST API");
		return personaInputAdapterRest.buscarPersona(database, cc);
	}

	@Operation(summary = "create: crea una persona en la base indicada en el body")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Persona creada"),
			@ApiResponse(responseCode = "400", description = "Base de datos o datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "La cédula ya existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse crearPersona(@RequestBody PersonaRequest request) throws InvalidOptionException {
		log.info("Into crearPersona REST API");
		return personaInputAdapterRest.crearPersona(request);
	}

	@Operation(summary = "edit: edita una persona (la cédula se toma de la ruta)")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Persona editada"),
			@ApiResponse(responseCode = "400", description = "Base de datos o datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "La persona no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@PutMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse editarPersona(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Cédula", example = "123456789") @PathVariable Integer cc,
			@RequestBody PersonaRequest request) throws InvalidOptionException, NoExistException {
		log.info("Into editarPersona REST API");
		return personaInputAdapterRest.editarPersona(database, cc, request);
	}

	@Operation(summary = "drop: elimina una persona (sin teléfonos ni estudios)")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Persona eliminada"),
			@ApiResponse(responseCode = "400", description = "Base de datos o cédula inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "La persona no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "La persona tiene teléfonos o estudios", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@DeleteMapping(path = "/{database}/{cc}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarPersona(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Cédula", example = "123456789") @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		log.info("Into eliminarPersona REST API");
		return personaInputAdapterRest.eliminarPersona(database, cc);
	}

	@Operation(summary = "getPhones: teléfonos de una persona")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Teléfonos de la persona"),
			@ApiResponse(responseCode = "400", description = "Base de datos o cédula inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "La persona no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}/{cc}/telefonos", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TelefonoResponse> telefonosPersona(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Cédula", example = "123456789") @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		log.info("Into telefonosPersona REST API");
		return personaInputAdapterRest.telefonosPersona(database, cc);
	}

	@Operation(summary = "getStudies: estudios de una persona")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Estudios de la persona"),
			@ApiResponse(responseCode = "400", description = "Base de datos o cédula inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "La persona no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}/{cc}/estudios", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EstudioResponse> estudiosPersona(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Cédula", example = "123456789") @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		log.info("Into estudiosPersona REST API");
		return personaInputAdapterRest.estudiosPersona(database, cc);
	}
}
