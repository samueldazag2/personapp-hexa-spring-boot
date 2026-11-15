package co.edu.javeriana.as.personapp.mapper;

import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.databaseName;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.optional;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.required;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.requiredInteger;
import static co.edu.javeriana.as.personapp.mapper.MapperRestUtils.text;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;

@Mapper
public class ProfesionMapperRest {

	public ProfesionResponse fromDomainToAdapterRest(Profession profession, DatabaseOption database) {
		return new ProfesionResponse(
				text(profession.getIdentification()),
				profession.getName(),
				text(profession.getDescription()),
				databaseName(database),
				"OK");
	}

	public Profession fromAdapterToDomain(ProfesionRequest request) {
		Profession profession = new Profession();
		profession.setIdentification(requiredInteger(request.getId(), "id"));
		profession.setName(required(request.getName(), "name"));
		profession.setDescription(optional(request.getDescription()));
		return profession;
	}
}
