# Cambios respecto a la plantilla

Plantilla del profesor: `b9bc9c824b8005f504163ca4cb67801c7b026df8`. Laboratorio antes de migrar: `db07b58` (`v1.0`). Migración integrada: `6386e31` (`v2.0`, en `main`). Referencias comprobadas con Git el 16 de noviembre de 2026.

Las tablas distinguen correcciones de código original, funciones nuevas y migración. Los siete módulos, paquetes, anotaciones de `common` y estructuras de persistencia proceden de la plantilla; se conservaron salvo los cambios identificados abajo. El autor de un commit no implica autoría exclusiva del archivo.

La plantilla incluía los siete módulos, `scripts/`, README, LICENSE, `.gitignore` y el POM raíz. El equipo añadió Docker, `.github/` para CI y revisión de PR, configuración de herramientas y `docs/` para trazabilidad y pruebas. `target/` y logs son artefactos generados e ignorados.

En las tablas se abrevia la ruta Java desde `src/main/java/co/edu/javeriana/as/personapp/`; Maria y Mongo corresponden a sus módulos de salida, REST y CLI a sus módulos de entrada. Los nombres permiten localizar los archivos con `rg --files`.

## A — Correcciones anteriores a esta migración

| Archivo | Método/bloque | Original | Problema | Cambio del grupo | Justificación | Componente | Responsable según Git | Commit |
|---|---|---|---|---|---|---|---|---|
| application/.../PersonUseCase.java | edit | Guardaba el objeto recibido sin fijar la identificación | Podía guardar otra llave | Asigna identification antes de save | Actualiza el registro solicitado | Caso de uso | samueldazag2 | 34a36dc |
| domain/.../Person.java | isValidAge, equals/hashCode | Edad nula no admitida; relaciones en igualdad | Nulos y recursión | Admite null y excluye listas | Evita errores al comparar grafos | Dominio | samueldazag2 | c6d1a44 |
| Maria y Mongo: EstudiosMapper*.java | fromAdapterToDomain | Retornaba null tras construir Study | Se perdía el estudio | Retorna study | Conserva el resultado del mapeo | Mapper de salida | samueldazag2 | 5545bab |
| Maria: PersonaMapperMaria, ProfesionMapperMaria, EstudiosMapperMaria, TelefonoMapperMaria | Mapeos completos y Shallow | Recorría ambos sentidos de las relaciones | Recursión; género no representable | Mapeos superficiales y validación M/F | Separa modelos sin expandir ciclos | Mapper de salida | samueldazag2 | d1c5e14 |
| Mongo: PersonaMapperMongo, ProfesionMapperMongo, EstudiosMapperMongo, TelefonoMapperMongo | Mapeos completos y Shallow | Recorría relaciones completas | Recursión; género inválido | Mapeos superficiales y validación M/F | Mantiene relaciones por identidad | Mapper de salida | samueldazag2 | b39ea98 |
| REST: PersonaMapperRest.java | fromAdapterToDomain | TODO devolvía Person vacío | Alta sin datos | Convierte y valida campos; representa edad nula vacía | Traduce DTO a dominio | Mapper de entrada | samueldazag2 | e40aae8 |
| REST: PersonaInputAdapterRest.java | crearPersona | Respuesta fija Maria; null ante error | Motor incorrecto y respuesta ausente | Selección del motor; posteriormente manejo HTTP centralizado | Delega por puertos y expone errores | Adaptador de entrada | samueldazag2 | e8f996a; 3dc8827 |
| Maria: EstudiosMapperMaria.java | fromDomainToAdapter, validateGraduationDate | No asignaba entidades relacionadas; conversión genérica de Date | Relaciones obligatorias y java.sql.Date | Asigna relaciones superficiales, convierte SQL Date con toLocalDate | Respeta FK y fecha sin usar toInstant no soportado | Mapper de salida | Sara Muñoz | 6b30adb |
| Mongo: PersonOutputAdapterMongo.java; MongoConfig.java | save; mongoCustomConversions | Absorbía error de escritura; fechas dependían de conversión | Falso éxito y desalineación de fechas | Propaga error; codecs Java Time nativos | El fallo llega al consumidor y fechas usan UTC | Persistencia | samueldazag2 | da59015 |
| Maria: PersonOutputAdapterMaria.java; ProfessionOutputAdapterMaria.java | delete | Sin rechazo explícito de relaciones en revisión previa | Borrado con referencias | Verifica relaciones y rechaza borrado | Mantiene integridad y HTTP 409 | Persistencia | samueldazag2 | 1647a63 |
| scripts/persona_dml_mongo.js; persona_ddl_mongo.js | selección de base | prueba_db; helper use | Init no cargaba en la base esperada | persona_db y db.getSiblingDB | Ejecutable en mongosh desde archivo | Infraestructura | samueldazag2 | 96c984c; 88c9d9b |

## B — Funcionalidades anteriores a esta migración

| Archivo | Método/bloque | Original | Faltante | Cambio del grupo | Justificación | Componente | Responsable según Git | Commit |
|---|---|---|---|---|---|---|---|---|
| application/.../port/in y port/out: Phone*, Profession*, Study* | interfaces | Solo contratos de Persona | CRUD de tres entidades | Nuevos puertos | Núcleo independiente de persistencia | Puertos | samueldazag2 | 8614d3a |
| application/.../usecase: PhoneUseCase, ProfessionUseCase, StudyUseCase | CRUD | Ausentes | Orquestación de tres entidades | Casos de uso y NoExistException | Reglas reutilizables desde REST/CLI | Aplicación | samueldazag2 | 42b32a8 |
| application/.../*UseCase.java; common/.../AlreadyExistsException.java, DatabaseOption.java | create, fromString | Sin rechazo de duplicados consistente | Alta distinta de actualización | Detecta llave existente; centraliza motor | Conserva semántica CRUD y selección MARIA/MONGO | Aplicación/común | samueldazag2 | 9ccb59f |
| maria-output-adapter: Phone/Profession/StudyOutputAdapterMaria, repositories y pruebas | CRUD, consultas de relaciones | Adaptadores ausentes o incompletos | Persistencia de tres entidades | Implementación Repository y mapeo | OutputPort no expone entidades JPA | Persistencia MariaDB | Sara Muñoz | 6b30adb |
| mongo-output-adapter: Phone/Profession/StudyOutputAdapterMongo, repositories y pruebas | CRUD, llave de estudio | Adaptadores ausentes o incompletos | Persistencia de tres entidades | Repositorios Mongo y mapeo | Respeta documentos y llave compuesta | Persistencia MongoDB | Sara Muñoz | 6b30adb |
| CLI: Phone/Profession/StudyInputAdapterCli, menús, EntityCrudMenu y CliInput | menú y CRUD | Solo Persona parcial | Operación por terminal | Adaptadores y menús de las cuatro entidades | Terminal delega a InputPort | Entrada CLI | SJVDC | db78c8a |
| CLI: PersonaMenu.java, PersonaInputAdapterCli.java | opciones 7 y 8 | Sin consultas de relaciones | Teléfonos y estudios de Persona | Consultas adicionales y errores de duplicado | Reutiliza casos de uso | Entrada CLI | samueldazag2 | 2cc0ee3 |
| REST: *ControllerV1, *InputAdapterRest, mappers, request/response y RestExceptionHandler | CRUD y errores | Persona parcial, otras entidades ausentes | API completa | Endpoints, DTO, HTTP 400/404/409 y selección de BD | Controller delega en adaptador, sin repositorios | Entrada REST | samueldazag2 | 3dc8827 |
| application/src/test; adaptadores src/test | pruebas | Sin cobertura del CRUD completo | Regresión | JUnit/Mockito, mappers, MockMvc y pruebas reales de persistencia | Verifica capas y relaciones | Pruebas | samueldazag2; Sara Muñoz (persistencia) | c67338a; 648969e; 4959b9a; 6b30adb; 98c3e5f; cb999c8 |
| Dockerfile.rest, Dockerfile.cli; docker-compose.yml | despliegue | Sin stack completo | Apps y dos bases | Construcción multietapa, healthchecks y scripts init | Despliegue reproducible | Infraestructura | Sara Muñoz (apps); samueldazag2 (Compose inicial) | 6b30adb; 542ffeb |
| scripts/persona_dml_maria.sql, persona_dml_mongo.js | datos | Datos iniciales parciales | Relaciones de ejemplo | Datos coherentes para cuatro entidades | Facilita validación en dos motores | Infraestructura | samueldazag2 | a6c8a64 |

## C — Migración tecnológica integrada en v2.0

Los cambios de esta sección están en `6386e31`, cuyo autor Git es Sara Muñoz. La autoría del commit identifica su registro, sin atribuir exclusivamente a una persona todo el trabajo técnico.

| Archivo | Bloque | Antes | Incompatibilidad | Cambio actual | Justificación | Componente | Responsable/commit |
|---|---|---|---|---|---|---|---|
| pom.xml | parent, propiedades, compiler | Java 11, Boot 2.7.18, Spring 5.3.31 y versiones manuales | Stack anterior a Jakarta/Java 25 | Java 25, parent Boot 4.1.2, gestión oficial y procesador Lombok explícito | Unifica dependencias/plugins; javac moderno requiere procesamiento explícito | Build | Sara Muñoz / 6386e31 |
| POM de application, common, Maria, Mongo, REST y CLI | dependencias | Spring fijado; MariaDB 3.1.4; JAXB 2 en CLI | Conflictos entre generaciones | Elimina versiones gestionadas y JAXB sin uso; starters WebMVC y springdoc 3.2.0 | No hay llamadas JAXB en el código; Hibernate aporta Jakarta JAXB | Build | Sara Muñoz / 6386e31 |
| domain/pom.xml | revisión | Solo herencia | Ninguna modificación necesaria | Se conserva | Dominio no necesita Spring Data | Dominio | Revisado, sin cambio |
| Maria: cinco entidades y cuatro adaptadores | imports y Transactional | javax.persistence/transaction | Hibernate usa Jakarta | jakarta.persistence/transaction | Mantiene tablas, llaves y transacciones | Persistencia | Sara Muñoz / 6386e31 |
| Maria: PersistenceIntegrationTest.java | EntityScan | Paquete anterior | Boot 4 reubicó anotación | boot.persistence.autoconfigure.EntityScan | Conserva descubrimiento JPA | Prueba | Sara Muñoz / 6386e31 |
| Mongo: PersistenceIntegrationTest.java; properties de REST/CLI; docker-compose.yml | conexión Mongo | spring.data.mongodb.* | Boot 4 renombró conexión | spring.mongodb.* y SPRING_MONGODB_* | Evita conectar silenciosamente a otra base | Configuración | Sara Muñoz / 6386e31 |
| Dockerfile.rest, Dockerfile.cli; .github/workflows/ci.yml | build/runtime | Builder 21, JRE 11; CI 17 | No ejecutan bytecode Java 25 | Builder/JRE/CI 25 y plugin Boot 4.1.2 | Misma versión de compilación y ejecución | Infraestructura | Sara Muñoz / 6386e31 |
| docker-compose.yml; docker-compose.validacion.yml | Mongo, aislamiento | Mongo 6.0 sin soporte | Base fuera de ciclo y colisiones con entorno local | Mongo 8.0 soportado; puertos/nombres/volumen nuevos para validar | No abre datos antiguos con otro motor | Infraestructura | Sara Muñoz / 6386e31 |
| README.md; documentación de migración | versiones, evidencia | Instrucciones Java 11/Boot 2 | Instalación desactualizada | Versiones, comandos y resultados reales | Sustentación reproducible | Documentación | Sara Muñoz / 6386e31 |
| PersonUseCase.java; EstudiosMapperMaria.java; EstudiosMapperMongo.java | edit y lectura de estudios | Código ya corregido | Razón poco evidente | Tres comentarios puntuales, sin cambiar lógica | Explica identidad y corte de recursión | Aplicación/mappers | Solo comentarios / 6386e31 |

## Decisiones de persistencia y compilación

- `StudyOutputAdapterMaria.save` asigna padres gestionados dentro de una transacción; guardar el hijo no crea padres. `EstudiosMapperMaria` asigna PK y relaciones, ambas necesarias para JPA.
- Las relaciones JPA de Persona/Profesión excluyen REMOVE y conservan PERSIST/MERGE (`6b30adb`). MariaDB protege los padres con FK y los adaptadores comprueban relaciones antes del borrado. Mongo verifica en código, sin atomicidad entre colecciones en standalone.
- `StudyUseCase.edit` conserva los padres del registro existente; Phone y Profession fijan el ID solicitado (`42b32a8`). No se cambian contratos de puertos.
- `java.sql.Date` usa `toLocalDate()`: `toInstant()` no está soportado. Mongo usa codecs Java Time UTC para evitar cambios de fecha según la zona de la JVM.
- `flatten-maven-plugin` resuelve `${revision}` en los POM instalados y permite ejecutar un módulo tras `install` (`b6bbc2e`). `release` verifica APIs y bytecode de la misma versión Java.
- Se conserva `Gender.OTHER` en el dominio; los mappers rechazan OTHER/null al escribir porque el esquema admite M/F. No se amplió el modelo persistido.

## Arquitectura y localización

Se conserva Controller/menú → InputAdapter → InputPort/UseCase → OutputPort → OutputAdapter → Repository → BD. No se cambian paquetes ni contratos. El dominio no importa Spring Data; los controladores no importan repositorios. Los mappers hijos usan relaciones superficiales, conservando el corte de recursión. Permanecen ciclos de inyección preexistentes entre mappers y `spring.main.allow-circular-references=true`; no se añadieron ciclos nuevos.

Para localizar líneas reales en cualquier checkout: `rg -n 'setIdentification|fromAdapterToDomainShallow|realCrudRelationshipsAndReferentialIntegrity|@.*Mapping' application maria-output-adapter mongo-output-adapter rest-input-adapter`. Para verificar atribución: `git show <commit> -- <archivo>` y `git blame origin/main -- <archivo>`. Los números de línea dependen del checkout y no se fijan artificialmente.

Fuentes oficiales verificadas: [Java LTS](https://www.oracle.com/java/technologies/java-se-support-roadmap.html), [Boot 4.1.2 y requisitos](https://docs.spring.io/spring-boot/system-requirements.html), [guía Boot 4](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide), [springdoc 3.2.0](https://springdoc.org/), [ciclo MongoDB](https://www.mongodb.com/legal/support-policy/lifecycles), [series MariaDB soportadas](https://mariadb.org/about/). Spring Boot es una versión estable soportada; no se le atribuye la clasificación LTS de Java. MongoDB 8.0 se elige por soporte de cinco años y menor salto que 9.0, no por ser la última serie publicada.
