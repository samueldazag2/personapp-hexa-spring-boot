package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;

@Adapter
public class ProfessionInputAdapterCli {

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	private ProfessionInputPort professionInputPort;

	public void setProfessionOutputPortInjection(String database) throws InvalidOptionException {
		if (DatabaseOption.MARIA.toString().equalsIgnoreCase(database)) {
			professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
		} else if (DatabaseOption.MONGO.toString().equalsIgnoreCase(database)) {
			professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + database);
		}
	}

	public Profession crear(Profession profession) {
		return professionInputPort.create(profession);
	}

	public Profession editar(Integer identification, Profession profession) throws NoExistException {
		return professionInputPort.edit(identification, profession);
	}

	public Boolean eliminar(Integer identification) throws NoExistException {
		return professionInputPort.drop(identification);
	}

	public Profession buscar(Integer identification) throws NoExistException {
		return professionInputPort.findOne(identification);
	}

	public List<Profession> listar() {
		return professionInputPort.findAll();
	}

	public Integer contar() {
		return professionInputPort.count();
	}
}
