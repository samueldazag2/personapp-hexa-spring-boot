package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

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
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;

@Adapter
public class PhoneInputAdapterCli {

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

	private PhoneInputPort phoneInputPort;
	private PersonInputPort personInputPort;

	public void setPhoneOutputPortInjection(String database) throws InvalidOptionException {
		if (DatabaseOption.MARIA.toString().equalsIgnoreCase(database)) {
			phoneInputPort = new PhoneUseCase(phoneOutputPortMaria);
			personInputPort = new PersonUseCase(personOutputPortMaria);
		} else if (DatabaseOption.MONGO.toString().equalsIgnoreCase(database)) {
			phoneInputPort = new PhoneUseCase(phoneOutputPortMongo);
			personInputPort = new PersonUseCase(personOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + database);
		}
	}

	public Person buscarPersona(Integer identification) throws NoExistException {
		return personInputPort.findOne(identification);
	}

	public Phone crear(Phone phone) {
		return phoneInputPort.create(phone);
	}

	public Phone editar(String number, Phone phone) throws NoExistException {
		return phoneInputPort.edit(number, phone);
	}

	public Boolean eliminar(String number) throws NoExistException {
		return phoneInputPort.drop(number);
	}

	public Phone buscar(String number) throws NoExistException {
		return phoneInputPort.findOne(number);
	}

	public List<Phone> listar() {
		return phoneInputPort.findAll();
	}

	public Integer contar() {
		return phoneInputPort.count();
	}
}
