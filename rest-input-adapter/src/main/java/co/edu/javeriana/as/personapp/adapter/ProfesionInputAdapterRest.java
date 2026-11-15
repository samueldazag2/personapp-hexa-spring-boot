package co.edu.javeriana.as.personapp.adapter;

import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.databaseName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.mapper.ProfesionMapperRest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterRest {

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private ProfesionMapperRest profesionMapperRest;

	private ProfessionInputPort professionInputPort(DatabaseOption database) {
		return new ProfessionUseCase(
				database == DatabaseOption.MARIA ? professionOutputPortMaria : professionOutputPortMongo);
	}

	public List<ProfesionResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial ProfesionEntity in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return professionInputPort(option).findAll().stream()
				.map(profession -> profesionMapperRest.fromDomainToAdapterRest(profession, option))
				.collect(Collectors.toList());
	}

	public ProfesionResponse crearProfesion(ProfesionRequest request) throws InvalidOptionException {
		log.info("Into crearProfesion in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(request.getDatabase());
		return profesionMapperRest.fromDomainToAdapterRest(
				professionInputPort(option).create(profesionMapperRest.fromAdapterToDomain(request)), option);
	}

	public ProfesionResponse buscarProfesion(String database, Integer id)
			throws InvalidOptionException, NoExistException {
		log.info("Into buscarProfesion in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return profesionMapperRest.fromDomainToAdapterRest(professionInputPort(option).findOne(id), option);
	}

	public ProfesionResponse editarProfesion(String database, Integer id, ProfesionRequest request)
			throws InvalidOptionException, NoExistException {
		log.info("Into editarProfesion in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		request.setId(String.valueOf(id));
		return profesionMapperRest.fromDomainToAdapterRest(
				professionInputPort(option).edit(id, profesionMapperRest.fromAdapterToDomain(request)), option);
	}

	public Response eliminarProfesion(String database, Integer id) throws InvalidOptionException, NoExistException {
		log.info("Into eliminarProfesion in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		Boolean dropped = professionInputPort(option).drop(id);
		return new Response(Boolean.TRUE.equals(dropped) ? "OK" : "ERROR",
				"profesion " + id + (Boolean.TRUE.equals(dropped) ? " eliminada de " : " no eliminada de ") + databaseName(option),
				LocalDateTime.now());
	}

	public CountResponse contarProfesiones(String database) throws InvalidOptionException {
		log.info("Into contarProfesiones in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return new CountResponse("profesion", databaseName(option), professionInputPort(option).count());
	}
}
