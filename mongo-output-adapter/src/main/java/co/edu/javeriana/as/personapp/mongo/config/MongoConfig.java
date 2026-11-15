package co.edu.javeriana.as.personapp.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

/**
 * Guarda y lee {@code LocalDate} con los codecs nativos del driver (UTC), de modo que la fecha de
 * grado de un estudio no cambie según la zona horaria de la máquina que ejecuta la aplicación.
 */
@Configuration
public class MongoConfig {

	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		return MongoCustomConversions.create(config -> config.useNativeDriverJavaTimeCodecs());
	}
}
