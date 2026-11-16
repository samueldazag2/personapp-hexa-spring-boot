# Comparación del trabajo local con GitHub

Fecha: 2026-11-16 (America/Bogota). Solo se leyó el repositorio original.

- Base remota obtenida mediante fetch en la copia: `db07b58f49964945144804149cae50d1ae2d657a`.
- Commit local conservado: `2d633c876cda3a6f2fdd0912849d19a1aefc360c`.
- 83 archivos preparados: 82 coinciden íntegramente con la base remota; uno tiene una diferencia exclusiva.
- El commit local cambia exclusivamente `ProfessionOutputAdapterMaria.java`: sustituye la anotación compuesta `@Adapter` por `@Component` y su import. La misma diferencia persiste en el índice. No se incorporó. La anotación compuesta ya incluye `@Component`; las pruebas de arranque verificarán su descubrimiento.
- Los demás cambios preparados ya están presentes en GitHub; no se reaplicaron parches.

| Archivo preparado | Comparación del contenido del índice con origin/main |
|---|---|
| `CONTRIBUTING.md` | Integrado: contenido idéntico |
| `README.md` | Integrado: contenido idéntico |
| `application/src/main/java/co/edu/javeriana/as/personapp/application/usecase/PersonUseCase.java` | Integrado: contenido idéntico |
| `application/src/main/java/co/edu/javeriana/as/personapp/application/usecase/PhoneUseCase.java` | Integrado: contenido idéntico |
| `application/src/main/java/co/edu/javeriana/as/personapp/application/usecase/ProfessionUseCase.java` | Integrado: contenido idéntico |
| `application/src/main/java/co/edu/javeriana/as/personapp/application/usecase/StudyUseCase.java` | Integrado: contenido idéntico |
| `application/src/test/java/co/edu/javeriana/as/personapp/application/usecase/PersonUseCaseTest.java` | Integrado: contenido idéntico |
| `application/src/test/java/co/edu/javeriana/as/personapp/application/usecase/PhoneUseCaseTest.java` | Integrado: contenido idéntico |
| `application/src/test/java/co/edu/javeriana/as/personapp/application/usecase/ProfessionUseCaseTest.java` | Integrado: contenido idéntico |
| `application/src/test/java/co/edu/javeriana/as/personapp/application/usecase/StudyUseCaseTest.java` | Integrado: contenido idéntico |
| `cli-input-adapter/pom.xml` | Integrado: contenido idéntico |
| `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/terminal/adapter/PersonaInputAdapterCli.java` | Integrado: contenido idéntico |
| `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/terminal/adapter/PhoneInputAdapterCli.java` | Integrado: contenido idéntico |
| `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/terminal/adapter/ProfessionInputAdapterCli.java` | Integrado: contenido idéntico |
| `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/terminal/adapter/StudyInputAdapterCli.java` | Integrado: contenido idéntico |
| `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/terminal/menu/CliInput.java` | Integrado: contenido idéntico |
| `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/terminal/menu/EntityCrudMenu.java` | Integrado: contenido idéntico |
| `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/terminal/menu/MenuPrincipal.java` | Integrado: contenido idéntico |
| `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/terminal/menu/PersonaMenu.java` | Integrado: contenido idéntico |
| `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/terminal/menu/PhoneMenu.java` | Integrado: contenido idéntico |
| `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/terminal/menu/ProfessionMenu.java` | Integrado: contenido idéntico |
| `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/terminal/menu/StudyMenu.java` | Integrado: contenido idéntico |
| `cli-input-adapter/src/test/java/co/edu/javeriana/as/personapp/terminal/menu/PersonaMenuTest.java` | Integrado: contenido idéntico |
| `common/src/main/java/co/edu/javeriana/as/personapp/common/exceptions/AlreadyExistsException.java` | Integrado: contenido idéntico |
| `common/src/main/java/co/edu/javeriana/as/personapp/common/setup/DatabaseOption.java` | Integrado: contenido idéntico |
| `docs/fix-plantilla.md` | Integrado: contenido idéntico |
| `docs/persona1/README.md` | Integrado: contenido idéntico |
| `docs/persona1/revision.md` | Integrado: contenido idéntico |
| `docs/persona2/GUIA_ESTUDIO.md` | Integrado: contenido idéntico |
| `docs/persona2/README.md` | Integrado: contenido idéntico |
| `docs/persona2/RESULTADOS.md` | Integrado: contenido idéntico |
| `docs/pruebas-funcionales.md` | Integrado: contenido idéntico |
| `docs/revision-final.md` | Integrado: contenido idéntico |
| `maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/adapter/PersonOutputAdapterMaria.java` | Integrado: contenido idéntico |
| `maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/adapter/ProfessionOutputAdapterMaria.java` | Exclusivo: @Component en lugar de @Adapter |
| `maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/entity/EstudiosEntity.java` | Integrado: contenido idéntico |
| `maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/entity/EstudiosEntityPK.java` | Integrado: contenido idéntico |
| `maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/entity/PersonaEntity.java` | Integrado: contenido idéntico |
| `maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/entity/ProfesionEntity.java` | Integrado: contenido idéntico |
| `maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/entity/TelefonoEntity.java` | Integrado: contenido idéntico |
| `maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/repository/PersonaRepositoryMaria.java` | Integrado: contenido idéntico |
| `maria-output-adapter/src/test/java/co/edu/javeriana/as/personapp/mariadb/adapter/PersonOutputAdapterMariaTest.java` | Integrado: contenido idéntico |
| `mongo-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mongo/adapter/PersonOutputAdapterMongo.java` | Integrado: contenido idéntico |
| `mongo-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mongo/adapter/ProfessionOutputAdapterMongo.java` | Integrado: contenido idéntico |
| `mongo-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mongo/config/MongoConfig.java` | Integrado: contenido idéntico |
| `mongo-output-adapter/src/test/java/co/edu/javeriana/as/personapp/mongo/adapter/PersonOutputAdapterMongoTest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/PersonAppRestApi.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/adapter/EstudioInputAdapterRest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/adapter/PersonaInputAdapterRest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/adapter/ProfesionInputAdapterRest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/adapter/TelefonoInputAdapterRest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/controller/EstudioControllerV1.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/controller/PersonaControllerV1.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/controller/ProfesionControllerV1.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/controller/RestExceptionHandler.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/controller/TelefonoControllerV1.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/mapper/EstudioMapperRest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/mapper/MapperRestUtils.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/mapper/PersonaMapperRest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/mapper/ProfesionMapperRest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/mapper/TelefonoMapperRest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/model/request/EstudioRequest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/model/request/PersonaRequest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/model/request/ProfesionRequest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/model/request/TelefonoRequest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/model/response/CountResponse.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/model/response/ErrorResponse.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/model/response/EstudioResponse.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/model/response/PersonaResponse.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/model/response/ProfesionResponse.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/model/response/Response.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/model/response/TelefonoResponse.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/test/java/co/edu/javeriana/as/personapp/adapter/EstudioInputAdapterRestTest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/test/java/co/edu/javeriana/as/personapp/adapter/PersonaInputAdapterRestTest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/test/java/co/edu/javeriana/as/personapp/adapter/ProfesionInputAdapterRestTest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/test/java/co/edu/javeriana/as/personapp/adapter/TelefonoInputAdapterRestTest.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/test/java/co/edu/javeriana/as/personapp/controller/EstudioControllerV1Test.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/test/java/co/edu/javeriana/as/personapp/controller/PersonaControllerV1Test.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/test/java/co/edu/javeriana/as/personapp/controller/ProfesionControllerV1Test.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/test/java/co/edu/javeriana/as/personapp/controller/TelefonoControllerV1Test.java` | Integrado: contenido idéntico |
| `rest-input-adapter/src/test/java/co/edu/javeriana/as/personapp/mapper/EntidadesMapperRestTest.java` | Integrado: contenido idéntico |
| `scripts/persona_dml_maria.sql` | Integrado: contenido idéntico |
| `scripts/persona_dml_mongo.js` | Integrado: contenido idéntico |

Diferencia exacta pendiente de revisión (no es un conflicto de merge porque no se hizo merge):

```diff
diff --git a/maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/adapter/ProfessionOutputAdapterMaria.java b/maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/adapter/ProfessionOutputAdapterMaria.java
index ba96595..5a94fac 100644
--- a/maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/adapter/ProfessionOutputAdapterMaria.java
+++ b/maria-output-adapter/src/main/java/co/edu/javeriana/as/personapp/mariadb/adapter/ProfessionOutputAdapterMaria.java
@@ -4,14 +4,14 @@ import java.util.List;
 import java.util.stream.Collectors;

 import org.springframework.beans.factory.annotation.Autowired;
+import org.springframework.stereotype.Component;

 import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
-import co.edu.javeriana.as.personapp.common.annotations.Adapter;
 import co.edu.javeriana.as.personapp.domain.Profession;
 import co.edu.javeriana.as.personapp.mariadb.mapper.ProfesionMapperMaria;
 import co.edu.javeriana.as.personapp.mariadb.repository.ProfesionRepositoryMaria;

-@Adapter("professionOutputAdapterMaria")
+@Component("professionOutputAdapterMaria")
 @javax.transaction.Transactional
 public class ProfessionOutputAdapterMaria implements ProfessionOutputPort {
```
