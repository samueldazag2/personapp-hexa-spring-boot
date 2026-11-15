package co.edu.javeriana.as.personapp.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;

class EntidadesMapperRestTest {

	private final ProfesionMapperRest profesionMapper = new ProfesionMapperRest();
	private final TelefonoMapperRest telefonoMapper = new TelefonoMapperRest();
	private final EstudioMapperRest estudioMapper = new EstudioMapperRest();

	@Test
	void profesionMapsFieldsAndEmptyDescriptionToNull() {
		Profession profession = profesionMapper.fromAdapterToDomain(new ProfesionRequest("7", "Abogado", " ", "MARIA"));

		assertEquals(7, profession.getIdentification());
		assertNull(profession.getDescription());
	}

	@Test
	void profesionResponseShowsEmptyDescriptionInsteadOfNull() {
		ProfesionResponse response = profesionMapper.fromDomainToAdapterRest(new Profession(7, "Abogado"), DatabaseOption.MONGO);

		assertEquals("", response.getDescription());
		assertEquals("MongoDB", response.getDatabase());
	}

	@Test
	void profesionRejectsMissingName() {
		assertThrows(IllegalArgumentException.class,
				() -> profesionMapper.fromAdapterToDomain(new ProfesionRequest("7", null, null, "MARIA")));
	}

	@Test
	void telefonoKeepsOwnerIdentification() {
		Phone phone = telefonoMapper.fromAdapterToDomain(new TelefonoRequest("3001234567", "Claro", "123", "MARIA"));

		assertEquals(123, phone.getOwner().getIdentification());
	}

	@Test
	void telefonoRejectsNonNumericOwner() {
		assertThrows(IllegalArgumentException.class,
				() -> telefonoMapper.fromAdapterToDomain(new TelefonoRequest("3001234567", "Claro", "abc", "MARIA")));
	}

	@Test
	void estudioParsesCompositeKeyAndDate() {
		Study study = estudioMapper.fromAdapterToDomain(new EstudioRequest("1", "123", "2020-06-30", "", "MARIA"));

		assertEquals(1, study.getProfession().getIdentification());
		assertEquals(123, study.getPerson().getIdentification());
		assertEquals(LocalDate.of(2020, 6, 30), study.getGraduationDate());
		assertNull(study.getUniversityName());
	}

	@Test
	void estudioRejectsInvalidDate() {
		assertThrows(IllegalArgumentException.class,
				() -> estudioMapper.fromAdapterToDomain(new EstudioRequest("1", "123", "30/06/2020", null, "MARIA")));
	}
}
