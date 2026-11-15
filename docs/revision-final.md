# Revisión final — Laboratorio 2

Auditoría del repositorio contra el enunciado *"Implementación de Servicio WEB en Hexagonal con
Repository y Service"* y contra la tabla de las 6 funciones del CRUD
(create, findAll, findOne, edit, drop, count) × 4 entidades × 2 bases × (REST + CLI).

- Rama: `revision-final` (desde `main` en `db78c8a`).
- Criterios aplicados: code review (correctitud), arquitectura hexagonal, estrategia de pruebas,
  documentación y limpieza de código.

> Las secciones 1–3 son el estado **ANTES** (auditoría inicial). La sección [Después](#después) es la
> auditoría repetida sobre el estado final de la rama.

## 1. Checklist del enunciado (antes)

| Requisito | Cumple | Evidencia |
|---|---|---|
| JDK 11 (`maven.compiler.release`) | sí | `pom.xml` (`global.java.version=11`) |
| Spring Boot (2.x) | sí | `pom.xml` (2.7.18) |
| MariaDB | sí | `maria-output-adapter/` (4 entidades) |
| MongoDB | sí | `mongo-output-adapter/` (4 entidades) |
| Arquitectura hexagonal (domain / application / adapters) | sí | módulos Maven |
| Patrón Repository | sí | `*/repository/*Repository{Maria,Mongo}.java` |
| Patrón Service (UseCase) | sí | `application/.../usecase/*UseCase.java` |
| REST | **parcial** | Solo `PersonaControllerV1` con GET listado y POST |
| CLI | sí (falta getPhones/getStudies) | `cli-input-adapter/.../menu/*Menu.java` |
| Swagger 3 (OpenAPI) | **parcial** | `springdoc-openapi-ui` presente, sin `@Tag`/`@Operation`/`@Schema` |
| CRUD profesion(id, nom, des) | **parcial** | Núcleo, persistencia y CLI; sin REST |
| CRUD persona(cc, nombre, apellido, genero M/F, edad) | **parcial** | REST solo 2 de 6 funciones |
| CRUD estudios(id_prof, cc_per, fecha, univer) PK compuesta | **parcial** | Núcleo, persistencia y CLI; sin REST |
| CRUD telefono(num, oper, duenio → persona.cc) | **parcial** | Núcleo, persistencia y CLI; sin REST |
| README (configuración, ambiente, compilación, despliegue) | **parcial** | `README.md` es guía de equipo, no describe REST completo ni endpoints |
| Script DDL | sí | `scripts/persona_ddl_maria.sql`, `scripts/persona_ddl_mongo.js` |
| Script DML | **parcial** | Solo personas en Maria y Mongo |
| Tag del repositorio | **no** | No existe `v1.0` |

## 2. Matriz entidad × función (antes)

| Entidad | Función | REST | CLI |
|---|---|---|---|
| Persona | create | ✅ (errores con HTTP 200) | ✅ |
| Persona | findAll | ✅ | ✅ |
| Persona | findOne | ❌ | ✅ |
| Persona | edit | ❌ | ✅ |
| Persona | drop | ❌ | ✅ |
| Persona | count | ❌ | ✅ |
| Persona | getPhones | ❌ | ❌ |
| Persona | getStudies | ❌ | ❌ |
| Profesión | 6 funciones | ❌ | ✅ |
| Estudio | 6 funciones | ❌ | ✅ |
| Teléfono | 6 funciones | ❌ | ✅ |

Ambos motores (MARIA y MONGO) tienen los 4 output adapters, así que la columna CLI aplica a las dos
bases. Orden actual de la CLI (consistente en las 4 entidades y se conserva):
`1 Crear, 2 Editar, 3 Eliminar, 4 Buscar por id, 5 Ver todos, 6 Contar, 0 Regresar`.

## 3. Hallazgos (antes)

| # | Archivo | Problema | Severidad | Corrección |
|---|---|---|---|---|
| H1 | `rest-input-adapter/` | No existen controladores REST de Profesión, Estudio y Teléfono; Persona solo tiene 2 de 6 funciones y no expone getPhones/getStudies. | crítica | Controller → InputAdapterRest → MapperRest + DTOs para las 4 entidades. |
| H2 | `rest-input-adapter/.../PersonaInputAdapterRest.java` | Los errores (BD inválida, datos inválidos) se devuelven con HTTP 200 y `status: "ERROR..."`; BD inválida en GET devuelve `[]`. | alta | Propagar excepciones y traducirlas en un `@RestControllerAdvice` (400/404/409) con JSON uniforme. |
| H3 | `rest-input-adapter/.../PersonaInputAdapterRest.java` | El caso de uso elegido se guarda en un campo del singleton (`personInputPort`): dos peticiones concurrentes a bases distintas pueden cruzarse. | alta | Elegir el UseCase en una variable local por petición. |
| H4 | `mongo-output-adapter/.../PersonOutputAdapterMongo.java` | `save` atrapa `MongoWriteException` y devuelve la persona sin guardar (pendiente 2 de `fix-plantilla.md`). | alta | Quitar el `try/catch`; el duplicado ya lo controla el UseCase. |
| H5 | `cli-input-adapter/.../EntityCrudMenu.java` | No atrapa `AlreadyExistsException` (nueva en esta rama): crear un id repetido terminaría la CLI. | alta | Agregarla al `catch`. |
| H6 | `cli-input-adapter/.../PersonaMenu.java` | Falta "Ver teléfonos" y "Ver estudios" (getPhones/getStudies). | media | Opciones 7 y 8 en el menú de Persona. |
| H7 | `scripts/persona_dml_maria.sql`, `persona_dml_mongo.js` | Solo traen personas; no hay profesiones, estudios ni teléfonos de ejemplo. | media | Mismos datos de ejemplo en ambas bases. |
| H8 | `rest-input-adapter/.../controller/*` | Swagger sin `@Tag`, `@Operation`, `@ApiResponse` ni ejemplos `@Schema`. | media | Anotaciones en los 4 controladores y DTOs. |
| H9 | `maria`/`mongo` output adapters | Borrar una persona/profesión con teléfonos o estudios lanza `DataIntegrityViolationException` (FK en Maria, verificación manual en Mongo) y termina en HTTP 500. | media | Traducir a 409 en el advice (no se borra en cascada: decisión conservadora). |
| H10 | `mongo-output-adapter` | `LocalDate` se guarda con la zona horaria de la JVM; fuera de Docker (UTC-5) la fecha de los estudios del DML se leería un día antes. | media | Usar los codecs nativos `java.time` del driver (UTC). |
| H11 | `PersonOutputAdapterMongo.java` | Campos `@Autowired` declarados a mitad de clase con nombres completamente calificados. | baja | Moverlos arriba con imports. |
| H12 | `PersonaRepositoryMaria.java` | Import sin usar (`org.springframework.stereotype.Repository`). | baja | Eliminarlo. |
| H13 | `*Entity.java` (Maria) | Comentarios `TODO` autogenerados por NetBeans en `equals`. | baja | Eliminar los TODO. |
| H14 | `rest-input-adapter/.../PersonaControllerV1.java` | `toUpperCase()` en el controlador y lógica de BD duplicada en cada adaptador. | baja | `DatabaseOption.fromString` (ya en `common`). |
| H15 | `README.md` | No describe endpoints, ejemplos, Swagger, DDL/DML ni matriz de verificación. | media | README completo en español. |
| H16 | `application/.../usecase/*UseCase.java` | Usa `@Qualifier` de Spring (heredado de la plantilla). | baja | Se mantiene: es parte de la plantilla del curso; `domain` sí está libre de Spring. |
| H17 | `PhoneUseCase`, `StudyUseCase` | No validan que existan la persona/profesión: el error llega como `DataIntegrityViolationException` desde el output adapter. | media | Los adaptadores de entrada (REST y CLI) buscan dueño/persona/profesión con `findOne` de su UseCase antes de crear/editar → 404. No se cambian los contratos de los puertos. |

### Pendientes de `docs/fix-plantilla.md`

1. `EstudiosMapperMaria.fromDomainToAdapter` sin persona/profesión → **resuelto** en `main`
   (usa `fromDomainToAdapterShallow` y `StudyOutputAdapterMaria.save` asigna las entidades gestionadas).
2. `PersonOutputAdapterMongo.save` se traga `MongoWriteException` → **no resuelto** (H4).

### Arquitectura

- `domain`: solo Lombok, sin Spring. ✅
- `application`: depende de `domain` y `common` (más `@Qualifier`, ver H16). ✅
- Los adaptadores de salida no dependen entre sí; los de entrada dependen de `application` y de los
  adaptadores de salida solo para el ensamblado (qualifiers). ✅
- Ningún controlador o menú llama repositorios ni output adapters directamente. ✅
- Mapeos sin recursión (métodos `*Shallow`), `equals/hashCode` sin listas. ✅

### Docker

- Dockerfiles multi-stage (Maven JDK 21 compila con `release 11` → JRE 11). ✅
- Compose con `healthcheck` en mariadb, mongodb y rest; `depends_on: service_healthy`. ✅
- Hosts por variables de entorno (`SPRING_DATASOURCE_URL`, `SPRING_DATA_MONGODB_HOST`) sin tocar los
  `application.properties` locales. ✅

## Después

Auditoría repetida sobre el estado final de `revision-final` (2026-11-15).

### Checklist del enunciado (después)

| Requisito | Cumple | Evidencia |
|---|---|---|
| JDK 11 (`maven.compiler.release`) | sí | `pom.xml` (sin cambios) |
| Spring Boot 2.x | sí | `pom.xml` (2.7.18, sin cambios) |
| MariaDB y MongoDB | sí | 4 output adapters por motor; DML con las 4 entidades |
| Hexagonal + Repository + Service | sí | Controller/Menu → InputAdapter → UseCase → OutputPort → OutputAdapter → Repository |
| REST | sí | `rest-input-adapter/.../controller/{Persona,Profesion,Estudio,Telefono}ControllerV1.java` |
| CLI | sí | `cli-input-adapter/.../menu/*Menu.java` (+ opciones 7 y 8 en Persona) |
| Swagger 3 | sí | `@Tag`, `@Operation`, `@ApiResponse`, `@Schema`; `/swagger-ui.html`, `/api-docs` (26 operaciones) |
| CRUD profesion / persona / estudios (PK compuesta) / telefono | sí | Matriz siguiente y `docs/pruebas-funcionales.md` |
| README (configuración, ambiente, compilación, despliegue) | sí | `README.md` |
| Script DDL y DML | sí | `scripts/` (mismos datos en MariaDB y MongoDB) |
| Tag del repositorio | sí | `v1.0` (se crea al final, ver reporte de entrega) |

### Matriz entidad × función (después)

| Entidad | create | findAll | findOne | edit | drop | count | extra |
|---|:-:|:-:|:-:|:-:|:-:|:-:|---|
| Persona | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | getPhones ✅, getStudies ✅ (REST y CLI) |
| Profesión | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | |
| Estudio | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | llave `/{idProf}/{ccPer}` |
| Teléfono | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | ✅ REST/CLI | |

Cada celda verificada en MARIA y en MONGO. **6 funciones × 4 entidades × 2 bases × (REST + CLI) = completo.**

### Estado de los hallazgos

| # | Severidad | Estado | Cambio |
|---|---|---|---|
| H1 | crítica | corregido | 4 controladores + 4 `*InputAdapterRest` + 4 mappers + DTOs request/response. |
| H2 | alta | corregido | `RestExceptionHandler` (`@RestControllerAdvice`): 400/404/409/500 con `{status, mensaje, timestamp}`; POST responde 201. |
| H3 | alta | corregido | `personInputPort(DatabaseOption)` crea el caso de uso por petición (variable local). |
| H4 | alta | corregido | `PersonOutputAdapterMongo.save` sin `try/catch`; prueba `saveDoesNotSwallowWriteErrors`. |
| H5 | alta | corregido | `EntityCrudMenu` atrapa `AlreadyExistsException`; prueba `duplicatedPersonDoesNotStopTheMenu`. |
| H6 | media | corregido | `PersonaMenu` opciones 7 y 8 mediante el gancho `printExtraOptions`/`handleExtraOption` de `EntityCrudMenu`. |
| H7 | media | corregido | DML con 4 profesiones, 4 estudios y 4 teléfonos, iguales en ambos motores. |
| H8 | media | corregido | Anotaciones OpenAPI en controladores y DTOs; `@OpenAPIDefinition` en `PersonAppRestApi`. |
| H9 | media | corregido | Maria y Mongo verifican relaciones antes de borrar → `DataIntegrityViolationException` → 409. |
| H10 | media | corregido | `MongoConfig` usa los codecs nativos `java.time` (UTC). |
| H11 | baja | corregido | Campos e imports ordenados en `PersonOutputAdapterMongo`. |
| H12 | baja | corregido | Import sin usar eliminado. |
| H13 | baja | corregido | TODOs autogenerados eliminados de las 5 entidades JPA. |
| H14 | baja | corregido | `DatabaseOption.fromString` en REST y CLI de Persona; sin `toUpperCase()` en el controlador. |
| H15 | media | corregido | README completo (arquitectura, ER, endpoints, Swagger, scripts, pruebas, matriz). |
| H16 | baja | se mantiene | `@Qualifier` en los casos de uso es parte de la plantilla del curso. |
| H17 | media | corregido | Validación de FK en los adaptadores de entrada REST (y ya existía en la CLI) → 404. |

Revisión de limpieza final: sin `printStackTrace`, sin `TODO`/`FIXME`, `System.out` solo en
`cli-input-adapter`, `domain` sin imports de Spring, ningún controlador/menú usa repositorios.

### Decisiones tomadas (conservadoras)

- **Cambios de comportamiento en los 2 endpoints previos de Persona:** se conservan rutas y forma de la
  respuesta exitosa; el POST ahora responde **201** (antes 200) y los errores responden 400/404/409 con
  el JSON uniforme (antes 200 con `status: "ERROR..."`), como pide el enunciado de la revisión.
- **409 Conflict** (no pedido explícitamente) para llave duplicada y para borrar con relaciones: es el
  código HTTP correcto y evita 500. No se borra en cascada para no perder datos.
- **`sex` en la respuesta de Persona** se mantiene como `MALE`/`FEMALE` (comportamiento previo); en el
  request se acepta `M`/`F`.
- **Orden de la CLI**: el existente (`1 crear, 2 editar, 3 eliminar, 4 buscar, 5 listar, 6 contar`) ya
  era consistente en las 4 entidades, así que se conservó y solo se agregaron 7 y 8 en Persona.
- **FK en adaptadores de entrada** y no en los casos de uso: así no cambian los contratos de los
  puertos ni el constructor de los casos de uso.

### Verificación

- `mvn clean install`: BUILD SUCCESS — 164 pruebas (162 ejecutadas + 2 de integración opcionales
  omitidas), 0 fallos. Antes: 87.
- `docker compose down -v && docker compose up -d --build`: mariadb, mongodb y rest `healthy`.
- curl: 71/71 casos correctos (48 de las 6 funciones × 4 entidades × 2 bases, 4 de getPhones/getStudies,
  19 de errores 400/404/409). Ver `docs/pruebas-funcionales.md`.
- Swagger: `/swagger-ui.html` → 302 → `/swagger-ui/index.html` 200; `/api-docs` con 4 tags y 26 operaciones.
- CLI en Docker: las 6 opciones de las 4 entidades en MariaDB y MongoDB, más 7 y 8 en Persona.
