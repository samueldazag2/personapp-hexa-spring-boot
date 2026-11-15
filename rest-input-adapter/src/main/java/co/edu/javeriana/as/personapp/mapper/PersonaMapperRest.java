package co.edu.javeriana.as.personapp.mapper;

import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.databaseName;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.optionalInteger;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.required;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.requiredInteger;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.text;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

@Mapper
public class PersonaMapperRest {

	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, DatabaseOption.MARIA);
	}

	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, DatabaseOption.MONGO);
	}

	public PersonaResponse fromDomainToAdapterRest(Person person, DatabaseOption database) {
		return new PersonaResponse(
				text(person.getIdentification()),
				person.getFirstName(),
				person.getLastName(),
				text(person.getAge()),
				text(person.getGender()),
				databaseName(database),
				"OK");
	}

	public Person fromAdapterToDomain(PersonaRequest request) {
		Person person = new Person();
		person.setIdentification(requiredInteger(request.getDni(), "dni"));
		person.setFirstName(required(request.getFirstName(), "firstName"));
		person.setLastName(required(request.getLastName(), "lastName"));
		person.setAge(validateAge(request.getAge()));
		person.setGender(validateGender(request.getSex()));
		return person;
	}

	private Integer validateAge(String age) {
		Integer value = optionalInteger(age, "age");
		if (value != null && value < 0)
			throw new IllegalArgumentException("age must be greater than or equal to 0: " + age);
		return value;
	}

	private Gender validateGender(String sex) {
		if ("M".equalsIgnoreCase(sex))
			return Gender.MALE;
		if ("F".equalsIgnoreCase(sex))
			return Gender.FEMALE;
		throw new IllegalArgumentException("sex must be M or F: " + sex);
	}

}
