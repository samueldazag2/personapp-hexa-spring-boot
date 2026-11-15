package co.edu.javeriana.as.personapp.adapter;

import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.databaseName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterRest {

	@Autowired
	@Qualifier("phoneOutputAdapterMaria")
	private PhoneOutputPort phoneOutputPortMaria;

	@Autowired
	@Qualifier("phoneOutputAdapterMongo")
	private PhoneOutputPort phoneOutputPortMongo;

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private TelefonoMapperRest telefonoMapperRest;

	private PhoneInputPort phoneInputPort(DatabaseOption database) {
		return new PhoneUseCase(database == DatabaseOption.MARIA ? phoneOutputPortMaria : phoneOutputPortMongo);
	}

	private PersonInputPort personInputPort(DatabaseOption database) {
		return new PersonUseCase(database == DatabaseOption.MARIA ? personOutputPortMaria : personOutputPortMongo);
	}

	/** Valida la llave foránea duenio -> persona.cc: el dueño debe existir en la misma base. */
	private Phone withExistingOwner(Phone phone, DatabaseOption option) throws NoExistException {
		phone.setOwner(personInputPort(option).findOne(phone.getOwner().getIdentification()));
		return phone;
	}

	public List<TelefonoResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial TelefonoEntity in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return phoneInputPort(option).findAll().stream()
				.map(phone -> telefonoMapperRest.fromDomainToAdapterRest(phone, option))
				.collect(Collectors.toList());
	}

	public TelefonoResponse crearTelefono(TelefonoRequest request) throws InvalidOptionException, NoExistException {
		log.info("Into crearTelefono in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(request.getDatabase());
		Phone phone = withExistingOwner(telefonoMapperRest.fromAdapterToDomain(request), option);
		return telefonoMapperRest.fromDomainToAdapterRest(phoneInputPort(option).create(phone), option);
	}

	public TelefonoResponse buscarTelefono(String database, String numero)
			throws InvalidOptionException, NoExistException {
		log.info("Into buscarTelefono in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return telefonoMapperRest.fromDomainToAdapterRest(phoneInputPort(option).findOne(numero), option);
	}

	public TelefonoResponse editarTelefono(String database, String numero, TelefonoRequest request)
			throws InvalidOptionException, NoExistException {
		log.info("Into editarTelefono in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		request.setNumber(numero);
		Phone phone = withExistingOwner(telefonoMapperRest.fromAdapterToDomain(request), option);
		return telefonoMapperRest.fromDomainToAdapterRest(phoneInputPort(option).edit(numero, phone), option);
	}

	public Response eliminarTelefono(String database, String numero) throws InvalidOptionException, NoExistException {
		log.info("Into eliminarTelefono in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		Boolean dropped = phoneInputPort(option).drop(numero);
		return new Response(Boolean.TRUE.equals(dropped) ? "OK" : "ERROR",
				"telefono " + numero + (Boolean.TRUE.equals(dropped) ? " eliminado de " : " no eliminado de ") + databaseName(option),
				LocalDateTime.now());
	}

	public CountResponse contarTelefonos(String database) throws InvalidOptionException {
		log.info("Into contarTelefonos in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return new CountResponse("telefono", databaseName(option), phoneInputPort(option).count());
	}
}
