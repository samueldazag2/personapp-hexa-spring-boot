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

import co.edu.javeriana.as.personapp.adapter.TelefonoInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.ErrorResponse;
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
@RequestMapping("/api/v1/telefono")
@Tag(name = "Telefono", description = "CRUD de telefono(num, oper, duenio -> persona.cc) en MARIA o MONGO")
public class TelefonoControllerV1 {

	private static final String DB = "Base de datos: MARIA o MONGO (sin importar mayúsculas)";

	@Autowired
	private TelefonoInputAdapterRest telefonoInputAdapterRest;

	@Operation(summary = "findAll: lista todos los teléfonos")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado de teléfonos"),
			@ApiResponse(responseCode = "400", description = "Base de datos inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TelefonoResponse> telefonos(@Parameter(description = DB, example = "MARIA") @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into telefonos REST API");
		return telefonoInputAdapterRest.historial(database);
	}

	@Operation(summary = "count: cuenta los teléfonos")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Total de teléfonos"),
			@ApiResponse(responseCode = "400", description = "Base de datos inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public CountResponse contarTelefonos(@Parameter(description = DB, example = "MARIA") @PathVariable String database)
			throws InvalidOptionException {
		log.info("Into contarTelefonos REST API");
		return telefonoInputAdapterRest.contarTelefonos(database);
	}

	@Operation(summary = "findOne: busca un teléfono por número")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Teléfono encontrado"),
			@ApiResponse(responseCode = "400", description = "Base de datos inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "El teléfono no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping(path = "/{database}/{numero}", produces = MediaType.APPLICATION_JSON_VALUE)
	public TelefonoResponse buscarTelefono(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Número", example = "3001234567") @PathVariable String numero)
			throws InvalidOptionException, NoExistException {
		log.info("Into buscarTelefono REST API");
		return telefonoInputAdapterRest.buscarTelefono(database, numero);
	}

	@Operation(summary = "create: crea un teléfono en la base indicada en el body (el dueño debe existir)")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Teléfono creado"),
			@ApiResponse(responseCode = "400", description = "Base de datos o datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "El dueño no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "El número ya existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public TelefonoResponse crearTelefono(@RequestBody TelefonoRequest request)
			throws InvalidOptionException, NoExistException {
		log.info("Into crearTelefono REST API");
		return telefonoInputAdapterRest.crearTelefono(request);
	}

	@Operation(summary = "edit: edita un teléfono (el número se toma de la ruta)")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Teléfono editado"),
			@ApiResponse(responseCode = "400", description = "Base de datos o datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "El teléfono o el dueño no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@PutMapping(path = "/{database}/{numero}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public TelefonoResponse editarTelefono(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Número", example = "3001234567") @PathVariable String numero,
			@RequestBody TelefonoRequest request) throws InvalidOptionException, NoExistException {
		log.info("Into editarTelefono REST API");
		return telefonoInputAdapterRest.editarTelefono(database, numero, request);
	}

	@Operation(summary = "drop: elimina un teléfono")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Teléfono eliminado"),
			@ApiResponse(responseCode = "400", description = "Base de datos inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "El teléfono no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@DeleteMapping(path = "/{database}/{numero}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response eliminarTelefono(@Parameter(description = DB, example = "MARIA") @PathVariable String database,
			@Parameter(description = "Número", example = "3001234567") @PathVariable String numero)
			throws InvalidOptionException, NoExistException {
		log.info("Into eliminarTelefono REST API");
		return telefonoInputAdapterRest.eliminarTelefono(database, numero);
	}
}
