package co.edu.javeriana.as.personapp.mapper;

import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.databaseName;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.required;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.requiredInteger;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.text;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@Mapper
public class TelefonoMapperRest {

	public TelefonoResponse fromDomainToAdapterRest(Phone phone, DatabaseOption database) {
		Person owner = phone.getOwner();
		return new TelefonoResponse(
				phone.getNumber(),
				phone.getCompany(),
				owner != null ? text(owner.getIdentification()) : "",
				owner != null ? fullName(owner) : "",
				databaseName(database),
				"OK");
	}

	/**
	 * El dueño queda solo con su cédula; el adaptador de entrada lo reemplaza por la persona
	 * consultada en la base elegida (validación de la llave foránea).
	 */
	public Phone fromAdapterToDomain(TelefonoRequest request) {
		Phone phone = new Phone();
		phone.setNumber(required(request.getNumber(), "number"));
		phone.setCompany(required(request.getCompany(), "company"));
		Person owner = new Person();
		owner.setIdentification(requiredInteger(request.getOwnerId(), "ownerId"));
		phone.setOwner(owner);
		return phone;
	}

	private String fullName(Person person) {
		return (text(person.getFirstName()) + " " + text(person.getLastName())).trim();
	}
}
