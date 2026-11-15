package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterCli {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperCli personaMapperCli;

	PersonInputPort personInputPort;

	public void setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		personInputPort = new PersonUseCase(DatabaseOption.fromString(dbOption) == DatabaseOption.MARIA
				? personOutputPortMaria
				: personOutputPortMongo);
	}

	public void historial() {
	    log.info("Into historial PersonaEntity in Input Adapter");
	    personInputPort.findAll().stream()
	        .map(personaMapperCli::fromDomainToAdapterCli)
	        .forEach(System.out::println);
	}

	public Person crear(Person person) {
		return personInputPort.create(person);
	}

	public Person editar(Integer identification, Person person) throws NoExistException {
		return personInputPort.edit(identification, person);
	}

	public Boolean eliminar(Integer identification) throws NoExistException {
		return personInputPort.drop(identification);
	}

	public Person buscar(Integer identification) throws NoExistException {
		return personInputPort.findOne(identification);
	}

	public Integer contar() {
		return personInputPort.count();
	}

	public List<Phone> verTelefonos(Integer identification) throws NoExistException {
		return personInputPort.getPhones(identification);
	}

	public List<Study> verEstudios(Integer identification) throws NoExistException {
		return personInputPort.getStudies(identification);
	}

}
