package co.edu.javeriana.as.personapp.adapter;

import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.databaseName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class EstudioInputAdapterRest {

	@Autowired
	@Qualifier("studyOutputAdapterMaria")
	private StudyOutputPort studyOutputPortMaria;

	@Autowired
	@Qualifier("studyOutputAdapterMongo")
	private StudyOutputPort studyOutputPortMongo;

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private EstudioMapperRest estudioMapperRest;

	private StudyInputPort studyInputPort(DatabaseOption database) {
		return new StudyUseCase(database == DatabaseOption.MARIA ? studyOutputPortMaria : studyOutputPortMongo);
	}

	private PersonInputPort personInputPort(DatabaseOption database) {
		return new PersonUseCase(database == DatabaseOption.MARIA ? personOutputPortMaria : personOutputPortMongo);
	}

	private ProfessionInputPort professionInputPort(DatabaseOption database) {
		return new ProfessionUseCase(
				database == DatabaseOption.MARIA ? professionOutputPortMaria : professionOutputPortMongo);
	}

	public List<EstudioResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial EstudiosEntity in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return studyInputPort(option).findAll().stream()
				.map(study -> estudioMapperRest.fromDomainToAdapterRest(study, option))
				.collect(Collectors.toList());
	}

	/** Valida las llaves foráneas: la persona y la profesión deben existir en la misma base. */
	public EstudioResponse crearEstudio(EstudioRequest request) throws InvalidOptionException, NoExistException {
		log.info("Into crearEstudio in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(request.getDatabase());
		Study study = estudioMapperRest.fromAdapterToDomain(request);
		study.setPerson(personInputPort(option).findOne(study.getPerson().getIdentification()));
		study.setProfession(professionInputPort(option).findOne(study.getProfession().getIdentification()));
		return estudioMapperRest.fromDomainToAdapterRest(studyInputPort(option).create(study), option);
	}

	public EstudioResponse buscarEstudio(String database, Integer idProf, Integer ccPer)
			throws InvalidOptionException, NoExistException {
		log.info("Into buscarEstudio in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return estudioMapperRest.fromDomainToAdapterRest(studyInputPort(option).findOne(idProf, ccPer), option);
	}

	/** La llave compuesta viene de la ruta; el caso de uso conserva la persona y profesión existentes. */
	public EstudioResponse editarEstudio(String database, Integer idProf, Integer ccPer, EstudioRequest request)
			throws InvalidOptionException, NoExistException {
		log.info("Into editarEstudio in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		request.setProfessionId(String.valueOf(idProf));
		request.setPersonId(String.valueOf(ccPer));
		return estudioMapperRest.fromDomainToAdapterRest(
				studyInputPort(option).edit(idProf, ccPer, estudioMapperRest.fromAdapterToDomain(request)), option);
	}

	public Response eliminarEstudio(String database, Integer idProf, Integer ccPer)
			throws InvalidOptionException, NoExistException {
		log.info("Into eliminarEstudio in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		Boolean dropped = studyInputPort(option).drop(idProf, ccPer);
		return new Response(Boolean.TRUE.equals(dropped) ? "OK" : "ERROR",
				"estudio (" + idProf + ", " + ccPer + ")"
						+ (Boolean.TRUE.equals(dropped) ? " eliminado de " : " no eliminado de ") + databaseName(option),
				LocalDateTime.now());
	}

	public CountResponse contarEstudios(String database) throws InvalidOptionException {
		log.info("Into contarEstudios in Input Adapter");
		DatabaseOption option = DatabaseOption.fromString(database);
		return new CountResponse("estudio", databaseName(option), studyInputPort(option).count());
	}
}
