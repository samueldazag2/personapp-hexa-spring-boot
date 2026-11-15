package co.edu.javeriana.as.personapp.mapper;

import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.databaseName;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.optional;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.optionalDate;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.requiredInteger;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.text;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;

@Mapper
public class EstudioMapperRest {

	public EstudioResponse fromDomainToAdapterRest(Study study, DatabaseOption database) {
		Person person = study.getPerson();
		Profession profession = study.getProfession();
		return new EstudioResponse(
				profession != null ? text(profession.getIdentification()) : "",
				person != null ? text(person.getIdentification()) : "",
				text(study.getGraduationDate()),
				text(study.getUniversityName()),
				profession != null ? text(profession.getName()) : "",
				person != null ? (text(person.getFirstName()) + " " + text(person.getLastName())).trim() : "",
				databaseName(database),
				"OK");
	}

	/**
	 * Persona y profesión quedan solo con su llave; el adaptador de entrada las reemplaza por las
	 * consultadas en la base elegida (validación de las llaves foráneas).
	 */
	public Study fromAdapterToDomain(EstudioRequest request) {
		Profession profession = new Profession();
		profession.setIdentification(requiredInteger(request.getProfessionId(), "professionId"));
		Person person = new Person();
		person.setIdentification(requiredInteger(request.getPersonId(), "personId"));
		Study study = new Study(person, profession);
		study.setGraduationDate(optionalDate(request.getGraduationDate(), "graduationDate"));
		study.setUniversityName(optional(request.getUniversityName()));
		return study;
	}
}
