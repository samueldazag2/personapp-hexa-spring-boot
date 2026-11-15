package co.edu.javeriana.as.personapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "PersonAPP API", version = "v1",
		description = "Servicio web hexagonal con Repository y Service: CRUD de persona, profesion, estudio y telefono sobre MariaDB y MongoDB"))
public class PersonAppRestApi {

	public static void main(String[] args) {
		log.info("Starting PersonAppRestApi ...");
		SpringApplication.run(PersonAppRestApi.class, args);
		log.info("Started PersonAppRestApi OK");
	}

}
