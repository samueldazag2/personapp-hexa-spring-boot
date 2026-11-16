# PersonAPP — Laboratorio 2

[![CI](https://github.com/samueldazag2/personapp-hexa-spring-boot/actions/workflows/ci.yml/badge.svg)](https://github.com/samueldazag2/personapp-hexa-spring-boot/actions/workflows/ci.yml)

Laboratorio de Arquitectura de Software de la Pontificia Universidad Javeriana:
servicio web hexagonal con patrones Repository y Service. Implementa CRUD de
Persona, Profesión, Estudios y Teléfono por REST y CLI, con persistencia
seleccionable en MariaDB o MongoDB. Cada operación usa un motor; no hay
sincronización entre las bases.

**Equipo:** Samuel Daza, Sara Muñoz y Samuel José Velandia.
**Versión integrada:** `v2.0` (`6386e31`).

- [Arquitectura y contratos](#arquitectura-y-contratos)
- [Requisitos y configuración](#requisitos-y-configuración)
- [Compilación y despliegue](#compilación-y-despliegue)
- [REST, CLI y Swagger](#rest-cli-y-swagger)
- [Bases y scripts](#bases-y-scripts)
- [Pruebas](#pruebas)
- [Cambios respecto a la plantilla](docs/CAMBIOS_RESPECTO_PLANTILLA.md)

## Arquitectura y contratos

```text
Controller / menú → InputAdapter → InputPort / UseCase
    → OutputPort → OutputAdapter → Repository → MariaDB o MongoDB
```

| Módulo Maven | Responsabilidad |
|---|---|
| common | Anotaciones compartidas, excepciones y DatabaseOption. |
| domain | Person, Profession, Study, Phone y Gender; sin Spring Data. |
| application | Puertos y casos de uso (Service). |
| maria-output-adapter | Entidades JPA, mappers y repositorios Spring Data JPA. |
| mongo-output-adapter | Documentos, mappers y repositorios Spring Data MongoDB. |
| rest-input-adapter | Controladores, adaptadores de entrada, DTO y mappers REST. |
| cli-input-adapter | Menús y adaptadores de terminal. |

Los adaptadores dependen de `application`, que depende de `domain` y `common`.
Los controladores y menús no acceden a repositorios. Las anotaciones de `common`
usan Spring internamente; los casos de uso conservan el `@Qualifier` de la plantilla.

- Entrada: `create`, `edit`, `drop`, `findAll`, `findOne`, `count`; Persona añade
  `getPhones` y `getStudies`. Se conserva el nombre original `setPersintence`.
- Salida: `save`, `delete`, `find`, `findById`. `findById` devuelve null si no existe;
  el caso de uso lo convierte en `NoExistException`. `delete` comprueba ausencia.
- IDs: Persona/Profesión Integer, Teléfono String, Estudios `(professionId, personId)`.
  `StudyUseCase.edit` conserva ambos padres del registro consultado y edita sus datos.
- Los ocho beans siguen `<person|profession|study|phone>OutputAdapter<Maria|Mongo>`.
- `DatabaseOption.fromString` selecciona el motor sin distinguir mayúsculas. En REST
  el caso de uso se crea por petición para evitar compartir esa selección entre hilos.
- Los mappers hijos usan `*Shallow` para no recorrer otra vez las listas de sus padres.
  Los ciclos de inyección previos requieren `spring.main.allow-circular-references=true`.

## Requisitos y configuración

| Tecnología | Versión |
|---|---|
| Java | 25 LTS, `maven.compiler.release=25` |
| Spring Boot | 4.1.2; gestiona Framework, Spring Data, JDBC y Lombok |
| OpenAPI | springdoc-openapi-starter-webmvc-ui 3.2.0 |
| Maven | 3.9.12 usado en la validación |
| Bases | MariaDB 10.11 y MongoDB 8.0 |
| Docker | Engine y Compose v2; override de validación requiere Compose 2.24.4+ |

```bash
git clone https://github.com/samueldazag2/personapp-hexa-spring-boot.git
cd personapp-hexa-spring-boot
java -version
mvn -version
docker compose version
```

Seleccionar JDK 25 mediante `JAVA_HOME` y `PATH` antes de compilar; comprobar el
JDK que muestra Maven. En el IDE, habilitar el procesamiento de anotaciones Lombok.
Los dos módulos de entrada incluyen `src/main/resources/application.properties`:

| Conexión | Valor local | En Docker |
|---|---|---|
| MariaDB | `jdbc:mariadb://localhost:3307/persona_db` | `SPRING_DATASOURCE_URL=jdbc:mariadb://mariadb:3306/persona_db` |
| MongoDB | localhost:27017, persona_db, auth admin | `SPRING_MONGODB_HOST=mongodb` |
| REST | Puerto 3000 | `REST_PORT` cambia el puerto del host |

Los scripts crean usuario y clave `persona_db` en ambos motores. `DB_USER`,
`DB_PASSWORD`, `MONGO_USER` y `MONGO_PASSWORD` cambian las credenciales usadas por
las aplicaciones en Compose; deben coincidir con usuarios ya creados. No modifican
scripts ni cuentas existentes. Boot 4 usa `spring.mongodb.*` / `SPRING_MONGODB_*`.

## Compilación y despliegue

Desde la raíz:

```bash
mvn clean install
docker compose up -d --build
docker compose ps
docker compose logs -f rest
docker compose --profile cli run --rm cli
```

Los Dockerfiles multietapa construyen con Maven/JDK 25, ejecutan pruebas unitarias
y empaquetan con Boot. El runtime es `eclipse-temurin:25-jre-noble`. REST y CLI
esperan bases saludables. El healthcheck REST comprueba el puerto TCP; la prueba
CRUD se realiza por separado.

Para ejecutar aplicaciones locales con bases en Docker:

```bash
docker compose up -d mariadb mongodb
mvn clean install
mvn -pl rest-input-adapter spring-boot:run
# En otra terminal:
mvn -pl cli-input-adapter spring-boot:run
```

Detener el REST Docker con `docker compose stop rest` si ocupa el puerto 3000.
`docker compose stop` detiene servicios sin eliminar datos. Los scripts init solo
se ejecutan con volúmenes vacíos; recrear aplicaciones no recarga los datos.
MongoDB 8 usa el volumen nuevo `personapp_mongodb8_data` por defecto. No abrir con
MongoDB 8 un volumen MongoDB 6 directamente: los datos anteriores requieren una
migración respaldada según la ruta oficial de actualización.

Validación sin colisiones con otros despliegues:

```bash
docker compose -p personapp-lts-validacion -f docker-compose.yml -f docker-compose.validacion.yml up -d --build mariadb mongodb rest
docker compose -p personapp-lts-validacion -f docker-compose.yml -f docker-compose.validacion.yml run --rm cli
```

El override usa nombres y volúmenes separados y puertos 13307 (MariaDB), 17017
(MongoDB), 13000 (REST). Para detenerlo, usar los mismos argumentos con `stop`.

## REST, CLI y Swagger

Base REST: `http://localhost:3000/api/v1`. `{db}` acepta MARIA o MONGO. En POST,
la base va en `database` del cuerpo; en las demás operaciones, en la ruta.
En PUT prevalece la llave de la ruta.

| Operación | Persona | Profesión | Estudios | Teléfono |
|---|---|---|---|---|
| create | `POST /persona` | `POST /profesion` | `POST /estudio` | `POST /telefono` |
| findAll | `GET /persona/{db}` | `GET /profesion/{db}` | `GET /estudio/{db}` | `GET /telefono/{db}` |
| findOne | `GET /persona/{db}/{cc}` | `GET /profesion/{db}/{id}` | `GET /estudio/{db}/{idProf}/{ccPer}` | `GET /telefono/{db}/{numero}` |
| edit | `PUT /persona/{db}/{cc}` | `PUT /profesion/{db}/{id}` | `PUT /estudio/{db}/{idProf}/{ccPer}` | `PUT /telefono/{db}/{numero}` |
| drop | `DELETE /persona/{db}/{cc}` | `DELETE /profesion/{db}/{id}` | `DELETE /estudio/{db}/{idProf}/{ccPer}` | `DELETE /telefono/{db}/{numero}` |
| count | `GET /persona/{db}/count` | `GET /profesion/{db}/count` | `GET /estudio/{db}/count` | `GET /telefono/{db}/count` |

Persona añade `GET /persona/{db}/{cc}/telefonos` y `GET /persona/{db}/{cc}/estudios`.
POST responde 201; consultas, edición, borrado y conteo responden 200.
`RestExceptionHandler` unifica errores como `{status, mensaje, timestamp}`:
400 para datos/base/JSON inválidos, 404 para registros o padres inexistentes y
409 para duplicados o borrado de padres referenciados. Persona recibe sex M/F
y responde MALE/FEMALE.

Swagger contiene los cuerpos y ejemplos de las cuatro entidades:
[UI](http://localhost:3000/swagger-ui/index.html) y
[OpenAPI JSON](http://localhost:3000/api-docs), con 4 tags y 26 operaciones.
En validación aislada se usa el puerto 13000.
Ejemplo: `curl http://localhost:3000/api/v1/persona/MARIA/123456789`.
En Windows, enviar JSON con acentos como UTF-8 desde archivo o stdin.

CLI: `1 Personas, 2 Profesiones, 3 Teléfonos, 4 Estudios, 0 Salir`. Cada módulo
permite elegir `1 MariaDB` o `2 MongoDB`, seguido de `1 Crear`, `2 Editar`,
`3 Eliminar`, `4 Buscar`, `5 Listar`, `6 Contar`, `0 Regresar`. Persona añade
`7 Ver teléfonos` y `8 Ver estudios`.

## Bases y scripts

| Dominio | MariaDB | MongoDB |
|---|---|---|
| Persona | persona.cc | persona._id |
| Profesión | profesion.id | profesion._id |
| Estudios | PK/FK (id_prof, cc_per) | `_id = "<cc_per>-<id_prof>"`; referencias a Persona/Profesión |
| Teléfono | telefono.num; FK duenio | telefono._id; referencia primaryDuenio |

Una persona tiene varios teléfonos y estudios; una profesión, varios estudios.
Las entradas buscan los padres en el mismo motor antes de guardar un hijo. JPA
carga padres gestionados dentro de una transacción. Se conservan PERSIST/MERGE y
se excluye REMOVE en las relaciones: no se borran hijos automáticamente.
MariaDB aplica PK/FK; MongoDB verifica relaciones en los adaptadores, usa referencias
de lectura en padres y no guarda hijos en cascada. Estas comprobaciones Mongo no
son una transacción entre colecciones: el servidor standalone no garantiza
integridad ante escrituras externas o concurrentes.

| Script | Contenido |
|---|---|
| scripts/persona_ddl_maria.sql | Usuario, esquema, cuatro tablas, PK y FK. |
| scripts/persona_dml_maria.sql | 5 personas, 4 profesiones, 4 estudios y 4 teléfonos. |
| scripts/persona_ddl_mongo.js | Usuario en admin con permisos sobre persona_db. |
| scripts/persona_dml_mongo.js | Los mismos registros en las cuatro colecciones. |

Compose los monta en `docker-entrypoint-initdb.d`, en orden DDL → DML. El DDL
MariaDB elimina/recrea el esquema: no ejecutarlo manualmente sobre datos que
deban conservarse. En la operación normal se reutilizan los volúmenes.

## Pruebas

```bash
mvn clean install
# Con ambas bases inicializadas y disponibles:
mvn clean install "-Dpersistence.integration=true"
```

El primero ejecuta 162 pruebas y omite las dos integraciones externas por su
condición existente; el segundo ejecuta las 164 contra bases reales. Los XML
se generan en `*/target/surefire-reports`; `target/` y logs se ignoran en Git.
GitHub Actions ejecuta `mvn clean install` con Java 25 en push y PR.

La [síntesis de pruebas](docs/PRUEBAS.md) conserva resultados, cobertura y comandos.
La [trazabilidad](docs/CAMBIOS_RESPECTO_PLANTILLA.md) explica correcciones,
funciones añadidas y migración con referencias Git.
