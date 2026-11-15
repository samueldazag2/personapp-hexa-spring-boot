package co.edu.javeriana.as.personapp.adapter;

import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.databaseName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterRest {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperRest personaMapperRest;

	@Autowired
	private TelefonoMapperRest telefonoMapperRest;

	@Autowired
	private EstudioMapperRest estudioMapperRest;

	/**
	 * Elige el caso de uso según la base pedida. Se crea uno por petición (variable local) para que
	 * dos peticiones concurrentes a bases distintas no se crucen.
	 */
	private PersonInputPort personInputPort(DatabaseOption database) {
		return new PersonUseCase(database == DatabaseOption.MARIA ? personOutputPortMaria : personOutputPortMongo);
	}

	public List<PersonaResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial PersonaEntity in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return personInputPort(option).findAll().stream()
				.map(person -> personaMapperRest.fromDomainToAdapterRest(person, option))
				.collect(Collectors.toList());
	}

	public PersonaResponse crearPersona(PersonaRequest request) throws InvalidOptionException {
		log.info("Into crearPersona in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(request.getDatabase());
		return personaMapperRest.fromDomainToAdapterRest(
				personInputPort(option).create(personaMapperRest.fromAdapterToDomain(request)), option);
	}

	public PersonaResponse buscarPersona(String database, Integer cc) throws InvalidOptionException, NoExistException {
		log.info("Into buscarPersona in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return personaMapperRest.fromDomainToAdapterRest(personInputPort(option).findOne(cc), option);
	}

	public PersonaResponse editarPersona(String database, Integer cc, PersonaRequest request)
			throws InvalidOptionException, NoExistException {
		log.info("Into editarPersona in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		request.setDni(String.valueOf(cc));
		return personaMapperRest.fromDomainToAdapterRest(
				personInputPort(option).edit(cc, personaMapperRest.fromAdapterToDomain(request)), option);
	}

	public Response eliminarPersona(String database, Integer cc) throws InvalidOptionException, NoExistException {
		log.info("Into eliminarPersona in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		Boolean dropped = personInputPort(option).drop(cc);
		return new Response(Boolean.TRUE.equals(dropped) ? "OK" : "ERROR",
				"persona " + cc + (Boolean.TRUE.equals(dropped) ? " eliminada de " : " no eliminada de ") + databaseName(option),
				LocalDateTime.now());
	}

	public CountResponse contarPersonas(String database) throws InvalidOptionException {
		log.info("Into contarPersonas in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return new CountResponse("persona", databaseName(option), personInputPort(option).count());
	}

	public List<TelefonoResponse> telefonosPersona(String database, Integer cc)
			throws InvalidOptionException, NoExistException {
		log.info("Into telefonosPersona in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return personInputPort(option).getPhones(cc).stream()
				.map(phone -> telefonoMapperRest.fromDomainToAdapterRest(phone, option))
				.collect(Collectors.toList());
	}

	public List<EstudioResponse> estudiosPersona(String database, Integer cc)
			throws InvalidOptionException, NoExistException {
		log.info("Into estudiosPersona in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return personInputPort(option).getStudies(cc).stream()
				.map(study -> estudioMapperRest.fromDomainToAdapterRest(study, option))
				.collect(Collectors.toList());
	}

}
