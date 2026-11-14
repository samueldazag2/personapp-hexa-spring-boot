package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

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
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

@Adapter
public class StudyInputAdapterCli {

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

	private StudyInputPort studyInputPort;
	private PersonInputPort personInputPort;
	private ProfessionInputPort professionInputPort;

	public void setStudyOutputPortInjection(String database) throws InvalidOptionException {
		if (DatabaseOption.MARIA.toString().equalsIgnoreCase(database)) {
			studyInputPort = new StudyUseCase(studyOutputPortMaria);
			personInputPort = new PersonUseCase(personOutputPortMaria);
			professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
		} else if (DatabaseOption.MONGO.toString().equalsIgnoreCase(database)) {
			studyInputPort = new StudyUseCase(studyOutputPortMongo);
			personInputPort = new PersonUseCase(personOutputPortMongo);
			professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + database);
		}
	}

	public Person buscarPersona(Integer identification) throws NoExistException {
		return personInputPort.findOne(identification);
	}

	public Profession buscarProfesion(Integer identification) throws NoExistException {
		return professionInputPort.findOne(identification);
	}

	public Study crear(Study study) {
		return studyInputPort.create(study);
	}

	public Study editar(Integer professionId, Integer personId, Study study) throws NoExistException {
		return studyInputPort.edit(professionId, personId, study);
	}

	public Boolean eliminar(Integer professionId, Integer personId) throws NoExistException {
		return studyInputPort.drop(professionId, personId);
	}

	public Study buscar(Integer professionId, Integer personId) throws NoExistException {
		return studyInputPort.findOne(professionId, personId);
	}

	public List<Study> listar() {
		return studyInputPort.findAll();
	}

	public Integer contar() {
		return studyInputPort.count();
	}
}
