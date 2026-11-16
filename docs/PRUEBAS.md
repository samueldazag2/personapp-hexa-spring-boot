# Pruebas del laboratorio

Evidencia de la migración integrada en `v2.0` (`6386e31`), ejecutada el
16 de noviembre de 2026 (America/Bogota). Se distingue de la verificación
posterior de limpieza documental.

## Compilación y cobertura automatizada

| Módulo | Unitarias aprobadas | Integración real | Cobertura |
|---|---:|---:|---|
| application | 38 | — | Seis funciones de cuatro casos de uso, inexistentes y duplicados. |
| maria-output-adapter | 25 | 1 | Adaptadores, mappers, género, fechas, PK/FK y borrado protegido. |
| mongo-output-adapter | 23 | 1 | Adaptadores, referencias, mappers y propagación de errores de escritura. |
| rest-input-adapter | 70 | — | MockMvc de cuatro controladores, DTO, relaciones y HTTP 400/404/409. |
| cli-input-adapter | 6 | — | Menú Persona, opciones 1–8 y manejo de errores. |

`mvn clean install`: **BUILD SUCCESS**, 162 aprobadas, 0 fallos/errores y 2
integraciones omitidas por su condición existente.
`mvn clean install "-Dpersistence.integration=true"`: **BUILD SUCCESS**,
164 aprobadas, 0 fallos/errores y 0 omitidas. Se usó Maven 3.9.12 con Temurin
25.0.2+10; el padre y los siete módulos compilaron con `release=25`.

Tras la limpieza documental, el 16 de noviembre de 2026 se repitió
`mvn clean install` con el mismo JDK/Maven: **BUILD SUCCESS** en 1 min 49 s,
162 aprobadas, 0 fallos/errores y 2 integraciones omitidas por su condición.
Esta comprobación no volvió a ejecutar Docker ni integración contra las bases.

Las dos clases `PersistenceIntegrationTest`, método
`realCrudRelationshipsAndReferentialIntegrity`, usan motores reales, IDs negativos
aleatorios y limpieza de sus registros en `finally`. Verifican CRUD, dos estudios
para una persona, relaciones inversas y rechazo del borrado de padres. MariaDB
también verifica PK/FK con SQL directo. La prueba
`sqlDateAndMissingRelationsRetainCompositeIdentifiers` cubre SQL Date y los
identificadores recuperados desde la PK de Estudios.

## Docker y pruebas funcionales

| Verificación del 16 de noviembre | Resultado observado |
|---|---|
| Construcción REST y CLI | Correcta, con pruebas unitarias durante el build |
| Java de ambos contenedores | Temurin 25.0.5+8 LTS |
| MariaDB / MongoDB | 10.11.19 / 8.0.38; ambos healthy |
| REST | healthy y conexión a ambos motores |
| Smoke HTTP | 111 respuestas con código esperado, más aserciones de contenido; sin fallos |
| OpenAPI y Swagger | HTTP 200 en /api-docs y /swagger-ui/index.html; 26 operaciones |
| CLI | Listado y conteo de las cuatro entidades en ambos motores; salida normal |

El smoke [`validar_rest_lts.py`](../scripts/validar_rest_lts.py) comprueba crear,
consultar, editar, listar, contar y eliminar las cuatro entidades en MARIA y MONGO.
Comprueba relaciones de Persona, duplicados, borrados protegidos, datos y motores
inválidos, HTTP 400/404/409 y contenido actualizado. Solo borra sus llaves de prueba
y verifica el retorno de los conteos iniciales. CLI mostró en cada motor 5 personas,
4 profesiones, 4 teléfonos y 4 estudios. La sesión de migración no automatizó los
diálogos de escritura CLI; los CRUD se verificaron por REST y persistencia.

La evidencia anterior, del 15 de noviembre, registró 71/71 casos REST: 48 CRUD,
4 consultas de relaciones y 19 errores (registros/padres inexistentes, edición
inexistente, sexo/fecha/base/id inválidos, duplicados y borrado de persona
referenciada). También se recorrieron los seis diálogos CRUD de las cuatro entidades
en CLI para ambos motores, más opciones 7/8 de Persona. Los comandos, respuestas y
extractos originales permanecen en el
[historial de v1.0](https://github.com/samueldazag2/personapp-hexa-spring-boot/blob/v1.0/docs/pruebas-funcionales.md).
Esta evidencia previa no sustituye la validación de la migración descrita arriba.

## Reproducción sin alterar datos existentes

Con JDK 25 seleccionado y Docker disponible, desde la raíz:

```powershell
mvn clean install
docker compose -p personapp-lts-validacion -f docker-compose.yml -f docker-compose.validacion.yml up -d --build mariadb mongodb rest
$env:P2_MARIA_URL="jdbc:mariadb://localhost:13307/persona_db"
$env:P2_MONGO_URI="mongodb://persona_db:persona_db@localhost:17017/persona_db?authSource=admin"
mvn clean install "-Dpersistence.integration=true"
python scripts/validar_rest_lts.py http://localhost:13000
docker compose -p personapp-lts-validacion -f docker-compose.yml -f docker-compose.validacion.yml run --rm cli
docker compose -p personapp-lts-validacion -f docker-compose.yml -f docker-compose.validacion.yml ps
```

También se admiten `P2_MARIA_USER` y `P2_MARIA_PASSWORD`. En Bash, definir las mismas
variables con `export`. Las bases requieren esquema y usuarios de los scripts;
el override usa volúmenes y puertos propios. No eliminar volúmenes para repetir
pruebas. Para detener este entorno, usar los mismos argumentos Compose con `stop`.
En Windows, detener cualquier JVM local que ejecute un JAR de `target/` antes de
`mvn clean`, pues mantiene el archivo abierto. Los resultados de una nueva ejecución
se consultan en `*/target/surefire-reports`.
