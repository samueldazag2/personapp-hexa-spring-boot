# Informe de migración LTS

Validado el 16 de noviembre de 2026, zona America/Bogota. Copia independiente:
`C:\Users\munoz\lab 2- arqui\personapp-migracion-lts`.
Rama: `feature/migracion-lts-documentacion`, desde `origin/main` actualizado
`db07b58f49964945144804149cae50d1ae2d657a`. Sin commits, pushes, merges ni tags.

## Versiones anteriores y efectivas

| Tecnología | Antes | Después verificado |
|---|---|---|
| Java objetivo | 11 | 25 LTS |
| JDK del entorno | PATH predeterminado 21.0.10 | Pruebas ejecutadas con Temurin 25.0.2+10 |
| Java de imágenes REST y CLI | Builder 21; runtime 11 | Temurin 25.0.5+8 LTS |
| Spring Boot | 2.7.18 | 4.1.2 |
| Spring Framework | 5.3.31 | 7.0.10, gestionado por Boot |
| Spring Data JPA | Gestionado por Boot 2.7 | 4.1.2 |
| Spring Data MongoDB | Gestionado por Boot 2.7 | 5.1.2 |
| MariaDB JDBC | 3.1.4 | 3.5.10 |
| Lombok | 1.18.36 | 1.18.48 |
| springdoc | springdoc-openapi-ui 1.7.0 | springdoc-openapi-starter-webmvc-ui 3.2.0 |
| Maven | 3.9.12 en Docker | 3.9.12 local y en Docker |
| Plugins compiler / surefire | Defaults previos | 3.15.0 / 3.5.6, gestionados por Boot |
| MariaDB servidor | 10.11 | Serie LTS conservada; ejecutado 10.11.19 |
| MongoDB servidor | 6.0, fuera de soporte | Serie soportada 8.0; ejecutado 8.0.38 |

Las versiones efectivas se obtuvieron del árbol Maven y de los procesos reales.
No se añadieron dependencias externas Snapshot, Milestone o RC. La coordenada
interna existente `0.0.1-SNAPSHOT` del laboratorio permanece; no es una dependencia
externa elegida durante la migración. Se conserva flatten-maven-plugin 1.5.0:
funcionó con el nuevo reactor y no fue necesario actualizarlo arbitrariamente.

## Cambios y conflictos

La [trazabilidad A/B/C](CAMBIOS_RESPECTO_PLANTILLA.md) distingue las correcciones y
funciones previas de los cambios actuales. Incluye justificación, capa, autor Git
y commit verificable; el [inventario](INVENTARIO_PLANTILLA.tsv) cubre los 137
archivos distintos frente al profesor. Se añadieron solo tres comentarios Java:
identificación en `PersonUseCase.edit` y corte de recursión en la lectura de los
dos `EstudiosMapper`. No se cambiaron puertos, endpoints, menús, llaves ni DDL/DML.

Migración: parent Boot, procesador Lombok explícito para javac 25, Jakarta JPA y
transacciones, EntityScan reubicado, propiedades Mongo renombradas, starters
WebMVC y pruebas WebMVC, springdoc 3, Java 25 en Docker/CI y README. Se quitaron
las dependencias JAXB 2 y su propiedad CLI sin uso: el código no invoca JAXB y
Hibernate obtiene Jakarta JAXB desde el stack gestionado. El POM de domain se
revisó y se conserva sin cambio de contenido.

El repositorio original conserva `main`, el commit `2d633c8` y los 83 archivos
preparados. De esos archivos, 82 coinciden con GitHub. El único contenido exclusivo
es `ProfessionOutputAdapterMaria`: `@Component` y su import en lugar de `@Adapter`.
El commit local modifica exclusivamente ese mismo bloque. No se reaplicó: las
pruebas reales demostraron que `@Adapter` descubre el bean correctamente. La
[comparación local](COMPARACION_TRABAJO_LOCAL.md) contiene el detalle de los 83
archivos y el diff exacto. No hay conflictos de merge generados; esa diferencia
queda para decisión del equipo al integrar.

## Compilación y pruebas

| Ejecución | Resultado | Aprobadas | Fallidas/errores | Omitidas |
|---|---|---:|---:|---:|
| `mvn clean install` con JDK 25 | BUILD SUCCESS, padre y siete módulos | 162 | 0 | 2 |
| `mvn clean install "-Dpersistence.integration=true"` con BD aisladas | BUILD SUCCESS, padre y siete módulos | 164 | 0 | 0 |
| Smoke HTTP contra REST Docker | 111 respuestas con código esperado, más aserciones de contenido | 111 | 0 | 0 |

Las dos omisiones del primer comando son las condiciones preexistentes de las
pruebas de persistencia; no se desactivaron pruebas para migrar. Desglose por
módulo: [unitarias](RESULTADOS_UNITARIOS.tsv), [integración](RESULTADOS_INTEGRACION.tsv).
Las pruebas reales de ambas bases validan CRUD, relaciones, integridad, fecha de
grado y limpieza con llaves de prueba. El smoke comprueba crear, consultar,
editar y eliminar las cuatro entidades en ambos motores, listados, conteos,
relaciones de Persona, duplicados, rechazo de borrado referenciado y HTTP
400/404/409. Verifica contenido actualizado y retorno de conteos a sus valores
iniciales tras la limpieza. Comando reproducible:

```powershell
python scripts/validar_rest_lts.py http://localhost:13000
```

Incidencia resuelta: el primer comando de integración pasó persistencia pero
falló en `clean` de REST porque el smoke local mantenía el JAR abierto en Windows.
Se detuvo solo ese proceso y se repitió el reactor completo, que aprobó las 164
pruebas. Se conserva `migration-integration-intento1.log`; no se ocultó el fallo.
También se corrigió una expectativa inicial del smoke: son 26 operaciones HTTP
que comparten rutas, no 20 rutas independientes. No requirió cambiar la API.

## Docker, REST, CLI y Swagger

Proyecto Compose `personapp-lts-validacion`, con override independiente. Puertos
13307 (MariaDB), 17017 (MongoDB) y 13000 (REST). Las imágenes finales se construyeron
con pruebas unitarias, sin `-DskipTests`. Se eliminó el repackage manual redundante:
el parent Boot ya configura el empaquetado ejecutable.

| Servicio | Resultado |
|---|---|
| MariaDB | healthy; 10.11.19; conexión y pruebas reales aprobadas |
| MongoDB | healthy; 8.0.38; conexión y pruebas reales aprobadas |
| REST | healthy; Java 25.0.5; 111 comprobaciones HTTP aprobadas |
| CLI | Java 25.0.5; cuatro menús, listado y conteo en MARIA y MONGO; salida normal |
| OpenAPI | `/api-docs` HTTP 200 y 26 operaciones documentadas |
| Swagger UI | `/swagger-ui/index.html` HTTP 200 |

CLI mostró en cada motor 5 personas, 4 profesiones, 4 teléfonos y 4 estudios.
Se probaron las funciones CLI de lectura/conteo de los cuatro módulos; no se
automatizaron todos los diálogos de escritura CLI. Los CRUD completos se
verificaron por REST y por los adaptadores reales de persistencia.

Imágenes finales verificadas:

- REST: `sha256:975f9dc5cd15f13d732a7f6d686a4707d5082ab7164e0cb2001caa5677e38d82`.
- CLI: `sha256:399567e1c21a70d411e26add0e6db19e2daa7ec09c019eb7980509a0bf9a8cef`.

Se inició Docker Desktop instalado en el perfil del usuario. Los servicios ajenos
ya existentes no se modificaron. No se eliminaron volúmenes. La validación creó
`personapp-lts-validacion_mariadb_data`, `personapp-lts-validacion_mongodb_data`
(prueba preliminar Mongo 6, conservada) y
`personapp-lts-validacion_mongodb8_data`. Mongo 8 usa el volumen nuevo; no hubo
conversión de datos preexistentes. Se canceló una construcción preliminar
redundante para conservar las imágenes finales construidas con pruebas.

Los servicios de validación quedan iniciados para revisión:
[Swagger](http://localhost:13000/swagger-ui/index.html). Para detenerlos sin
eliminar datos, ejecutar el comando `stop` documentado en README.

## Requisitos y límites

Verificados: siete módulos, arquitectura hexagonal, Repository/Service, CRUD de
las cuatro entidades en MariaDB y MongoDB, relaciones e integridad, selección
MARIA/MONGO, REST, menú CLI, Swagger, imágenes Java 25 y documentación reproducible.
Se conservan DDL/DML y las pruebas. El dominio no depende de Spring Data y los
controladores no acceden a repositorios. Se mantienen mapeos superficiales y los
ciclos de inyección previos entre mappers; no se crean ciclos nuevos.

Persisten avisos compatibles de Hibernate (`@Temporal` sobre Date), Lombok
(`Unsafe`) y Mockito (carga dinámica de agente). No fallan pruebas ni compilación;
se mantienen los modelos y no se silencian verificaciones. Las etiquetas Docker
de serie resuelven el parche actual; las versiones e IDs de esta ejecución se
registran arriba. El JDK predeterminado del equipo sigue siendo 21: para compilar
la copia debe seleccionarse JDK 25, sin cambiar la configuración global.

Pendientes deliberados: revisión humana e integración en el repositorio único,
commit/push/tag final y ejecución de GitHub Actions en remoto. No se hicieron por
instrucción expresa. Tampoco se migraron datos de bases existentes; no era seguro
ni necesario para validar esta copia. No quedan fallos funcionales observados en
el alcance ejecutado.

## Evidencia y archivos

Los logs completos están en la raíz de la copia y se ignoran por la regla
existente `*.log`: `migration-build.log`, `migration-dependencies.log`,
`migration-integration.log`, `migration-docker-build.log`, `migration-cli.log`,
`migration-rest-docker-smoke.log` y los intentos preliminares. Los XML de la última
ejecución están en `*/target/surefire-reports`. El estado Git completo, lista de
archivos creados/modificados y `git diff --stat` se guardan en
[ESTADO_GIT.txt](ESTADO_GIT.txt). Los archivos nuevos no forman parte de las
estadísticas de `git diff` hasta que el usuario decida agregarlos al índice.
