# Pruebas funcionales — Laboratorio 2

Evidencia para la sección **Procedimiento** del informe. Todas las pruebas se ejecutaron el
2026-11-15 contra el ambiente Docker recién creado:

```bash
docker compose down -v && docker compose up -d --build
docker compose ps        # mariadb, mongodb y rest: (healthy)
```

- API: `http://localhost:3000/api/v1` · Swagger UI: `http://localhost:3000/swagger-ui.html`
  (302 → `/swagger-ui/index.html`, 200) · OpenAPI: `http://localhost:3000/api-docs`
  (4 tags: Persona, Profesion, Estudio, Telefono; 26 operaciones).
- Cobertura: 6 funciones (create, findAll, findOne, edit, drop, count) × 4 entidades × 2 bases = 48
  casos, + getPhones/getStudies de Persona en ambas bases (4), + errores 404/400/409.
- Códigos: 201 al crear; 200 en consultas, edición, borrado y count; 404 si no existe (también si la
  persona/profesión referenciada no existe); 400 con base o datos inválidos; 409 si la llave ya existe o
  si se intenta borrar una persona/profesión con registros asociados.

> Nota para Windows/Git Bash: si el cuerpo JSON lleva tildes o `ñ`, envíelo como UTF-8
> (`printf '%s' '{...}' | curl ... --data-binary @-`, o desde un archivo con `-d @archivo.json`).
> Con `-d '...'` directo en Git Bash los acentos llegan con otra codificación y la API responde 400
> ("Request body is missing or is not valid JSON"). En Linux/macOS `-d` funciona igual.

## CLI (ambos motores)

`docker compose --profile cli run --rm -T cli < entrada.txt` con una sesión que, para MariaDB (1) y luego
MongoDB (2), recorre en las 4 entidades: 1 Crear → 4 Buscar por id → 2 Editar → 5 Ver todos →
6 Contar → 3 Eliminar, y en Persona además 7 Ver teléfonos y 8 Ver estudios. Extracto de la salida
(se repite igual para MongoDB):

```text
Profesión creada: Profession(identification=20, name=Piloto, description=Aviacion civil)
Profesión actualizada: Profession(identification=20, name=Piloto comercial, description=)
Total de profesiones: 5
Profesión eliminada.
Persona creada: Person(identification=222333444, firstName=Juan, lastName=Rios, gender=MALE, age=40)
Persona actualizada: Person(identification=222333444, firstName=Juan, lastName=Rios Mora, gender=MALE, age=41)
Total de personas: 6
Teléfonos de la persona 123456789: 2
Estudios de la persona 321654987: 2
Persona eliminada.
Teléfono creado: Phone(number=3123334455, company=Claro, owner=Person(identification=123456789, firstName=Pepe, ...))
Teléfono actualizado: Phone(number=3123334455, company=Movistar, owner=Person(identification=123456789, ...))
Total de teléfonos: 5
Teléfono eliminado.
Estudio creado: Study(person=Person(identification=147258369, ...), profession=Profession(identification=1, ...), graduationDate=2024-01-01, universityName=EAN)
Estudio actualizado: Study(..., graduationDate=2024-02-02, universityName=EAN Bogota)
Total de estudios: 5
Estudio eliminado.
```

Menú de cada entidad (mismo orden en las 4): `1 crear, 2 editar, 3 eliminar, 4 buscar por
identificador, 5 listar todos, 6 contar, 0 regresar`; en Persona además `7 ver teléfonos` y
`8 ver estudios`.

## REST (curl)

## Base MARIA

Se crean registros nuevos (profesión 10, persona 111222333, teléfono 3112223344 y el estudio (10, 111222333)), se consultan, editan, cuentan y al final se eliminan, dejando la base como la dejó el DML.

### 1. [MARIA] Profesión · create — HTTP 201 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/profesion -H 'Content-Type: application/json' -d '{"id":"10","name":"Arquitecto","description":"Diseño de edificaciones","database":"MARIA"}'
```

```json
{
  "id": "10",
  "name": "Arquitecto",
  "description": "Diseño de edificaciones",
  "database": "MariaDB",
  "status": "OK"
}
```

### 2. [MARIA] Persona · create — HTTP 201 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/persona -H 'Content-Type: application/json' -d '{"dni":"111222333","firstName":"Laura","lastName":"Gomez","age":"25","sex":"F","database":"maria"}'
```

```json
{
  "dni": "111222333",
  "firstName": "Laura",
  "lastName": "Gomez",
  "age": "25",
  "sex": "FEMALE",
  "database": "MariaDB",
  "status": "OK"
}
```

### 3. [MARIA] Teléfono · create — HTTP 201 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/telefono -H 'Content-Type: application/json' -d '{"number":"3112223344","company":"Tigo","ownerId":"111222333","database":"MARIA"}'
```

```json
{
  "number": "3112223344",
  "company": "Tigo",
  "ownerId": "111222333",
  "database": "MariaDB",
  "ownerName": "Laura Gomez",
  "status": "OK"
}
```

### 4. [MARIA] Estudio · create — HTTP 201 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/estudio -H 'Content-Type: application/json' -d '{"professionId":"10","personId":"111222333","graduationDate":"2020-06-30","universityName":"Universidad Nacional","database":"MARIA"}'
```

```json
{
  "professionId": "10",
  "personId": "111222333",
  "graduationDate": "2020-06-30",
  "universityName": "Universidad Nacional",
  "database": "MariaDB",
  "professionName": "Arquitecto",
  "personName": "Laura Gomez",
  "status": "OK"
}
```

### 5. [MARIA] Profesión · findAll — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/profesion/MARIA
```

```json
[
  {
    "id": "1",
    "name": "Ingeniero de Sistemas",
    "description": "Diseño y desarrollo de software",
    "database": "MariaDB",
    "status": "OK"
  },
  {
    "id": "2",
    "name": "Medico",
    "description": "Diagnóstico y tratamiento de enfermedades",
    "database": "MariaDB",
    "status": "OK"
  },
  {
    "id": "3",
    "name": "Abogado",
    "description": "Asesoría y representación legal",
    "database": "MariaDB",
    "status": "OK"
  },
  {
    "id": "4",
    "name": "Contador",
    "description": "Contabilidad y finanzas",
    "database": "MariaDB",
    "status": "OK"
  },
  {
    "id": "10",
    "name": "Arquitecto",
    "description": "Diseño de edificaciones",
    "database": "MariaDB",
    "status": "OK"
  }
]
```

### 6. [MARIA] Persona · findAll — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/maria
```

```json
[
  {
    "dni": "111222333",
    "firstName": "Laura",
    "lastName": "Gomez",
    "age": "25",
    "sex": "FEMALE",
    "database": "MariaDB",
    "status": "OK"
  },
  {
    "dni": "123456789",
    "firstName": "Pepe",
    "lastName": "Perez",
    "age": "30",
    "sex": "MALE",
    "database": "MariaDB",
    "status": "OK"
  },
  {
    "dni": "147258369",
    "firstName": "Pepita",
    "lastName": "Juarez",
    "age": "10",
    "sex": "FEMALE",
    "database": "MariaDB",
    "status": "OK"
  },
  {
    "dni": "321654987",
    "firstName": "Pepa",
    "lastName": "Juarez",
    "age": "30",
    "sex": "FEMALE",
    "database": "MariaDB",
    "status": "OK"
  },
  {
    "dni": "963852741",
    "firstName": "Fede",
    "lastName": "Perez",
    "age": "18",
    "sex": "MALE",
    "database": "MariaDB",
    "status": "OK"
  },
  {
    "dni": "987654321",
    "firstName": "Pepito",
    "lastName": "Perez",
    "age": "",
    "sex": "MALE",
    "database": "MariaDB",
    "status": "OK"
  }
]
```

### 7. [MARIA] Teléfono · findAll — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/telefono/MARIA
```

```json
[
  {
    "number": "3001234567",
    "company": "Claro",
    "ownerId": "123456789",
    "database": "MariaDB",
    "ownerName": "Pepe Perez",
    "status": "OK"
  },
  {
    "number": "3109876543",
    "company": "Movistar",
    "ownerId": "123456789",
    "database": "MariaDB",
    "ownerName": "Pepe Perez",
    "status": "OK"
  },
  {
    "number": "3112223344",
    "company": "Tigo",
    "ownerId": "111222333",
    "database": "MariaDB",
    "ownerName": "Laura Gomez",
    "status": "OK"
  },
  {
    "number": "3157778899",
    "company": "Claro",
    "ownerId": "987654321",
    "database": "MariaDB",
    "ownerName": "Pepito Perez",
    "status": "OK"
  },
  {
    "number": "3204567890",
    "company": "Tigo",
    "ownerId": "321654987",
    "database": "MariaDB",
    "ownerName": "Pepa Juarez",
    "status": "OK"
  }
]
```

### 8. [MARIA] Estudio · findAll — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/estudio/MARIA
```

```json
[
  {
    "professionId": "1",
    "personId": "123456789",
    "graduationDate": "2015-12-15",
    "universityName": "Pontificia Universidad Javeriana",
    "database": "MariaDB",
    "professionName": "Ingeniero de Sistemas",
    "personName": "Pepe Perez",
    "status": "OK"
  },
  {
    "professionId": "2",
    "personId": "987654321",
    "graduationDate": "2018-06-30",
    "universityName": "Universidad Nacional de Colombia",
    "database": "MariaDB",
    "professionName": "Medico",
    "personName": "Pepito Perez",
    "status": "OK"
  },
  {
    "professionId": "3",
    "personId": "321654987",
    "graduationDate": "2012-11-20",
    "universityName": "Universidad de los Andes",
    "database": "MariaDB",
    "professionName": "Abogado",
    "personName": "Pepa Juarez",
    "status": "OK"
  },
  {
    "professionId": "4",
    "personId": "321654987",
    "graduationDate": "2016-06-10",
    "universityName": "Universidad del Rosario",
    "database": "MariaDB",
    "professionName": "Contador",
    "personName": "Pepa Juarez",
    "status": "OK"
  },
  {
    "professionId": "10",
    "personId": "111222333",
    "graduationDate": "2020-06-30",
    "universityName": "Universidad Nacional",
    "database": "MariaDB",
    "professionName": "Arquitecto",
    "personName": "Laura Gomez",
    "status": "OK"
  }
]
```

### 9. [MARIA] Profesión · findOne — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/profesion/MARIA/10
```

```json
{
  "id": "10",
  "name": "Arquitecto",
  "description": "Diseño de edificaciones",
  "database": "MariaDB",
  "status": "OK"
}
```

### 10. [MARIA] Persona · findOne — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/MARIA/111222333
```

```json
{
  "dni": "111222333",
  "firstName": "Laura",
  "lastName": "Gomez",
  "age": "25",
  "sex": "FEMALE",
  "database": "MariaDB",
  "status": "OK"
}
```

### 11. [MARIA] Teléfono · findOne — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/telefono/MARIA/3112223344
```

```json
{
  "number": "3112223344",
  "company": "Tigo",
  "ownerId": "111222333",
  "database": "MariaDB",
  "ownerName": "Laura Gomez",
  "status": "OK"
}
```

### 12. [MARIA] Estudio · findOne — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/estudio/MARIA/10/111222333
```

```json
{
  "professionId": "10",
  "personId": "111222333",
  "graduationDate": "2020-06-30",
  "universityName": "Universidad Nacional",
  "database": "MariaDB",
  "professionName": "Arquitecto",
  "personName": "Laura Gomez",
  "status": "OK"
}
```

### 13. [MARIA] Profesión · edit — HTTP 200 ✅

```bash
curl -s -X PUT http://localhost:3000/api/v1/profesion/MARIA/10 -H 'Content-Type: application/json' -d '{"name":"Arquitecta","description":"Diseño urbano"}'
```

```json
{
  "id": "10",
  "name": "Arquitecta",
  "description": "Diseño urbano",
  "database": "MariaDB",
  "status": "OK"
}
```

### 14. [MARIA] Persona · edit — HTTP 200 ✅

```bash
curl -s -X PUT http://localhost:3000/api/v1/persona/MARIA/111222333 -H 'Content-Type: application/json' -d '{"firstName":"Laura","lastName":"Gomez Diaz","age":"26","sex":"F"}'
```

```json
{
  "dni": "111222333",
  "firstName": "Laura",
  "lastName": "Gomez Diaz",
  "age": "26",
  "sex": "FEMALE",
  "database": "MariaDB",
  "status": "OK"
}
```

### 15. [MARIA] Teléfono · edit — HTTP 200 ✅

```bash
curl -s -X PUT http://localhost:3000/api/v1/telefono/MARIA/3112223344 -H 'Content-Type: application/json' -d '{"company":"WOM","ownerId":"111222333"}'
```

```json
{
  "number": "3112223344",
  "company": "WOM",
  "ownerId": "111222333",
  "database": "MariaDB",
  "ownerName": "Laura Gomez Diaz",
  "status": "OK"
}
```

### 16. [MARIA] Estudio · edit — HTTP 200 ✅

```bash
curl -s -X PUT http://localhost:3000/api/v1/estudio/MARIA/10/111222333 -H 'Content-Type: application/json' -d '{"graduationDate":"2021-12-10","universityName":"Universidad de Antioquia"}'
```

```json
{
  "professionId": "10",
  "personId": "111222333",
  "graduationDate": "2021-12-10",
  "universityName": "Universidad de Antioquia",
  "database": "MariaDB",
  "professionName": "Arquitecta",
  "personName": "Laura Gomez Diaz",
  "status": "OK"
}
```

### 17. [MARIA] Profesión · count — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/profesion/MARIA/count
```

```json
{
  "entity": "profesion",
  "database": "MariaDB",
  "count": 5
}
```

### 18. [MARIA] Persona · count — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/MARIA/count
```

```json
{
  "entity": "persona",
  "database": "MariaDB",
  "count": 6
}
```

### 19. [MARIA] Teléfono · count — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/telefono/MARIA/count
```

```json
{
  "entity": "telefono",
  "database": "MariaDB",
  "count": 5
}
```

### 20. [MARIA] Estudio · count — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/estudio/MARIA/count
```

```json
{
  "entity": "estudio",
  "database": "MariaDB",
  "count": 5
}
```

### 21. [MARIA] Persona · getPhones — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/MARIA/111222333/telefonos
```

```json
[
  {
    "number": "3112223344",
    "company": "WOM",
    "ownerId": "111222333",
    "database": "MariaDB",
    "ownerName": "Laura Gomez Diaz",
    "status": "OK"
  }
]
```

### 22. [MARIA] Persona · getStudies — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/MARIA/111222333/estudios
```

```json
[
  {
    "professionId": "10",
    "personId": "111222333",
    "graduationDate": "2021-12-10",
    "universityName": "Universidad de Antioquia",
    "database": "MariaDB",
    "professionName": "Arquitecta",
    "personName": "Laura Gomez Diaz",
    "status": "OK"
  }
]
```

### 23. [MARIA] Estudio · drop — HTTP 200 ✅

```bash
curl -s -X DELETE http://localhost:3000/api/v1/estudio/MARIA/10/111222333
```

```json
{
  "status": "OK",
  "description": "estudio (10, 111222333) eliminado de MariaDB",
  "date": "15/11/2026"
}
```

### 24. [MARIA] Teléfono · drop — HTTP 200 ✅

```bash
curl -s -X DELETE http://localhost:3000/api/v1/telefono/MARIA/3112223344
```

```json
{
  "status": "OK",
  "description": "telefono 3112223344 eliminado de MariaDB",
  "date": "15/11/2026"
}
```

### 25. [MARIA] Persona · drop — HTTP 200 ✅

```bash
curl -s -X DELETE http://localhost:3000/api/v1/persona/MARIA/111222333
```

```json
{
  "status": "OK",
  "description": "persona 111222333 eliminada de MariaDB",
  "date": "15/11/2026"
}
```

### 26. [MARIA] Profesión · drop — HTTP 200 ✅

```bash
curl -s -X DELETE http://localhost:3000/api/v1/profesion/MARIA/10
```

```json
{
  "status": "OK",
  "description": "profesion 10 eliminada de MariaDB",
  "date": "15/11/2026"
}
```

### 27. [MARIA] Error 404 · persona inexistente — HTTP 404 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/MARIA/1
```

```json
{
  "status": 404,
  "mensaje": "The person with id 1 does not exist into db, cannot be found",
  "timestamp": "2026-11-15T20:05:24"
}
```

### 28. [MARIA] Error 404 · estudio inexistente — HTTP 404 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/estudio/MARIA/10/111222333
```

```json
{
  "status": 404,
  "mensaje": "The study with profession id 10 and person id 111222333 does not exist into db, cannot be found",
  "timestamp": "2026-11-15T20:05:24"
}
```

### 29. [MARIA] Error 404 · teléfono con dueño inexistente — HTTP 404 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/telefono -H 'Content-Type: application/json' -d '{"number":"3000000000","company":"Claro","ownerId":"999","database":"MARIA"}'
```

```json
{
  "status": 404,
  "mensaje": "The person with id 999 does not exist into db, cannot be found",
  "timestamp": "2026-11-15T20:05:24"
}
```

### 30. [MARIA] Error 404 · editar profesión inexistente — HTTP 404 ✅

```bash
curl -s -X PUT http://localhost:3000/api/v1/profesion/MARIA/99 -H 'Content-Type: application/json' -d '{"name":"X"}'
```

```json
{
  "status": 404,
  "mensaje": "The profession with id 99 does not exist into db, cannot be edited",
  "timestamp": "2026-11-15T20:05:25"
}
```

### 31. [MARIA] Error 400 · sexo inválido — HTTP 400 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/persona -H 'Content-Type: application/json' -d '{"dni":"5","firstName":"A","lastName":"B","sex":"X","database":"MARIA"}'
```

```json
{
  "status": 400,
  "mensaje": "sex must be M or F: X",
  "timestamp": "2026-11-15T20:05:25"
}
```

### 32. [MARIA] Error 400 · fecha inválida — HTTP 400 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/estudio -H 'Content-Type: application/json' -d '{"professionId":"1","personId":"147258369","graduationDate":"30/06/2020","database":"MARIA"}'
```

```json
{
  "status": 400,
  "mensaje": "graduationDate must be a date with format yyyy-MM-dd: 30/06/2020",
  "timestamp": "2026-11-15T20:05:26"
}
```

### 33. [MARIA] Error 409 · cédula duplicada — HTTP 409 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/persona -H 'Content-Type: application/json' -d '{"dni":"123456789","firstName":"Pepe","lastName":"Perez","sex":"M","database":"MARIA"}'
```

```json
{
  "status": 409,
  "mensaje": "The person with id 123456789 already exists into db, cannot be created",
  "timestamp": "2026-11-15T20:05:26"
}
```

### 34. [MARIA] Error 409 · borrar persona con teléfonos — HTTP 409 ✅

```bash
curl -s -X DELETE http://localhost:3000/api/v1/persona/MARIA/123456789
```

```json
{
  "status": 409,
  "mensaje": "The person with id 123456789 has phones or studies, cannot be dropped",
  "timestamp": "2026-11-15T20:05:26"
}
```

## Base MONGO

Se crean registros nuevos (profesión 10, persona 111222333, teléfono 3112223344 y el estudio (10, 111222333)), se consultan, editan, cuentan y al final se eliminan, dejando la base como la dejó el DML.

### 35. [MONGO] Profesión · create — HTTP 201 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/profesion -H 'Content-Type: application/json' -d '{"id":"10","name":"Arquitecto","description":"Diseño de edificaciones","database":"MONGO"}'
```

```json
{
  "id": "10",
  "name": "Arquitecto",
  "description": "Diseño de edificaciones",
  "database": "MongoDB",
  "status": "OK"
}
```

### 36. [MONGO] Persona · create — HTTP 201 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/persona -H 'Content-Type: application/json' -d '{"dni":"111222333","firstName":"Laura","lastName":"Gomez","age":"25","sex":"F","database":"mongo"}'
```

```json
{
  "dni": "111222333",
  "firstName": "Laura",
  "lastName": "Gomez",
  "age": "25",
  "sex": "FEMALE",
  "database": "MongoDB",
  "status": "OK"
}
```

### 37. [MONGO] Teléfono · create — HTTP 201 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/telefono -H 'Content-Type: application/json' -d '{"number":"3112223344","company":"Tigo","ownerId":"111222333","database":"MONGO"}'
```

```json
{
  "number": "3112223344",
  "company": "Tigo",
  "ownerId": "111222333",
  "database": "MongoDB",
  "ownerName": "Laura Gomez",
  "status": "OK"
}
```

### 38. [MONGO] Estudio · create — HTTP 201 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/estudio -H 'Content-Type: application/json' -d '{"professionId":"10","personId":"111222333","graduationDate":"2020-06-30","universityName":"Universidad Nacional","database":"MONGO"}'
```

```json
{
  "professionId": "10",
  "personId": "111222333",
  "graduationDate": "2020-06-30",
  "universityName": "Universidad Nacional",
  "database": "MongoDB",
  "professionName": "Arquitecto",
  "personName": "Laura Gomez",
  "status": "OK"
}
```

### 39. [MONGO] Profesión · findAll — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/profesion/MONGO
```

```json
[
  {
    "id": "1",
    "name": "Ingeniero de Sistemas",
    "description": "Diseño y desarrollo de software",
    "database": "MongoDB",
    "status": "OK"
  },
  {
    "id": "2",
    "name": "Medico",
    "description": "Diagnóstico y tratamiento de enfermedades",
    "database": "MongoDB",
    "status": "OK"
  },
  {
    "id": "3",
    "name": "Abogado",
    "description": "Asesoría y representación legal",
    "database": "MongoDB",
    "status": "OK"
  },
  {
    "id": "4",
    "name": "Contador",
    "description": "Contabilidad y finanzas",
    "database": "MongoDB",
    "status": "OK"
  },
  {
    "id": "10",
    "name": "Arquitecto",
    "description": "Diseño de edificaciones",
    "database": "MongoDB",
    "status": "OK"
  }
]
```

### 40. [MONGO] Persona · findAll — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/mongo
```

```json
[
  {
    "dni": "123456789",
    "firstName": "Pepe",
    "lastName": "Perez",
    "age": "30",
    "sex": "MALE",
    "database": "MongoDB",
    "status": "OK"
  },
  {
    "dni": "987654321",
    "firstName": "Pepito",
    "lastName": "Perez",
    "age": "",
    "sex": "MALE",
    "database": "MongoDB",
    "status": "OK"
  },
  {
    "dni": "321654987",
    "firstName": "Pepa",
    "lastName": "Juarez",
    "age": "30",
    "sex": "FEMALE",
    "database": "MongoDB",
    "status": "OK"
  },
  {
    "dni": "147258369",
    "firstName": "Pepita",
    "lastName": "Juarez",
    "age": "10",
    "sex": "FEMALE",
    "database": "MongoDB",
    "status": "OK"
  },
  {
    "dni": "963852741",
    "firstName": "Fede",
    "lastName": "Perez",
    "age": "18",
    "sex": "MALE",
    "database": "MongoDB",
    "status": "OK"
  },
  {
    "dni": "111222333",
    "firstName": "Laura",
    "lastName": "Gomez",
    "age": "25",
    "sex": "FEMALE",
    "database": "MongoDB",
    "status": "OK"
  }
]
```

### 41. [MONGO] Teléfono · findAll — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/telefono/MONGO
```

```json
[
  {
    "number": "3001234567",
    "company": "Claro",
    "ownerId": "123456789",
    "database": "MongoDB",
    "ownerName": "Pepe Perez",
    "status": "OK"
  },
  {
    "number": "3109876543",
    "company": "Movistar",
    "ownerId": "123456789",
    "database": "MongoDB",
    "ownerName": "Pepe Perez",
    "status": "OK"
  },
  {
    "number": "3157778899",
    "company": "Claro",
    "ownerId": "987654321",
    "database": "MongoDB",
    "ownerName": "Pepito Perez",
    "status": "OK"
  },
  {
    "number": "3204567890",
    "company": "Tigo",
    "ownerId": "321654987",
    "database": "MongoDB",
    "ownerName": "Pepa Juarez",
    "status": "OK"
  },
  {
    "number": "3112223344",
    "company": "Tigo",
    "ownerId": "111222333",
    "database": "MongoDB",
    "ownerName": "Laura Gomez",
    "status": "OK"
  }
]
```

### 42. [MONGO] Estudio · findAll — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/estudio/MONGO
```

```json
[
  {
    "professionId": "1",
    "personId": "123456789",
    "graduationDate": "2015-12-15",
    "universityName": "Pontificia Universidad Javeriana",
    "database": "MongoDB",
    "professionName": "Ingeniero de Sistemas",
    "personName": "Pepe Perez",
    "status": "OK"
  },
  {
    "professionId": "2",
    "personId": "987654321",
    "graduationDate": "2018-06-30",
    "universityName": "Universidad Nacional de Colombia",
    "database": "MongoDB",
    "professionName": "Medico",
    "personName": "Pepito Perez",
    "status": "OK"
  },
  {
    "professionId": "3",
    "personId": "321654987",
    "graduationDate": "2012-11-20",
    "universityName": "Universidad de los Andes",
    "database": "MongoDB",
    "professionName": "Abogado",
    "personName": "Pepa Juarez",
    "status": "OK"
  },
  {
    "professionId": "4",
    "personId": "321654987",
    "graduationDate": "2016-06-10",
    "universityName": "Universidad del Rosario",
    "database": "MongoDB",
    "professionName": "Contador",
    "personName": "Pepa Juarez",
    "status": "OK"
  },
  {
    "professionId": "10",
    "personId": "111222333",
    "graduationDate": "2020-06-30",
    "universityName": "Universidad Nacional",
    "database": "MongoDB",
    "professionName": "Arquitecto",
    "personName": "Laura Gomez",
    "status": "OK"
  }
]
```

### 43. [MONGO] Profesión · findOne — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/profesion/MONGO/10
```

```json
{
  "id": "10",
  "name": "Arquitecto",
  "description": "Diseño de edificaciones",
  "database": "MongoDB",
  "status": "OK"
}
```

### 44. [MONGO] Persona · findOne — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/MONGO/111222333
```

```json
{
  "dni": "111222333",
  "firstName": "Laura",
  "lastName": "Gomez",
  "age": "25",
  "sex": "FEMALE",
  "database": "MongoDB",
  "status": "OK"
}
```

### 45. [MONGO] Teléfono · findOne — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/telefono/MONGO/3112223344
```

```json
{
  "number": "3112223344",
  "company": "Tigo",
  "ownerId": "111222333",
  "database": "MongoDB",
  "ownerName": "Laura Gomez",
  "status": "OK"
}
```

### 46. [MONGO] Estudio · findOne — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/estudio/MONGO/10/111222333
```

```json
{
  "professionId": "10",
  "personId": "111222333",
  "graduationDate": "2020-06-30",
  "universityName": "Universidad Nacional",
  "database": "MongoDB",
  "professionName": "Arquitecto",
  "personName": "Laura Gomez",
  "status": "OK"
}
```

### 47. [MONGO] Profesión · edit — HTTP 200 ✅

```bash
curl -s -X PUT http://localhost:3000/api/v1/profesion/MONGO/10 -H 'Content-Type: application/json' -d '{"name":"Arquitecta","description":"Diseño urbano"}'
```

```json
{
  "id": "10",
  "name": "Arquitecta",
  "description": "Diseño urbano",
  "database": "MongoDB",
  "status": "OK"
}
```

### 48. [MONGO] Persona · edit — HTTP 200 ✅

```bash
curl -s -X PUT http://localhost:3000/api/v1/persona/MONGO/111222333 -H 'Content-Type: application/json' -d '{"firstName":"Laura","lastName":"Gomez Diaz","age":"26","sex":"F"}'
```

```json
{
  "dni": "111222333",
  "firstName": "Laura",
  "lastName": "Gomez Diaz",
  "age": "26",
  "sex": "FEMALE",
  "database": "MongoDB",
  "status": "OK"
}
```

### 49. [MONGO] Teléfono · edit — HTTP 200 ✅

```bash
curl -s -X PUT http://localhost:3000/api/v1/telefono/MONGO/3112223344 -H 'Content-Type: application/json' -d '{"company":"WOM","ownerId":"111222333"}'
```

```json
{
  "number": "3112223344",
  "company": "WOM",
  "ownerId": "111222333",
  "database": "MongoDB",
  "ownerName": "Laura Gomez Diaz",
  "status": "OK"
}
```

### 50. [MONGO] Estudio · edit — HTTP 200 ✅

```bash
curl -s -X PUT http://localhost:3000/api/v1/estudio/MONGO/10/111222333 -H 'Content-Type: application/json' -d '{"graduationDate":"2021-12-10","universityName":"Universidad de Antioquia"}'
```

```json
{
  "professionId": "10",
  "personId": "111222333",
  "graduationDate": "2021-12-10",
  "universityName": "Universidad de Antioquia",
  "database": "MongoDB",
  "professionName": "Arquitecta",
  "personName": "Laura Gomez Diaz",
  "status": "OK"
}
```

### 51. [MONGO] Profesión · count — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/profesion/MONGO/count
```

```json
{
  "entity": "profesion",
  "database": "MongoDB",
  "count": 5
}
```

### 52. [MONGO] Persona · count — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/MONGO/count
```

```json
{
  "entity": "persona",
  "database": "MongoDB",
  "count": 6
}
```

### 53. [MONGO] Teléfono · count — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/telefono/MONGO/count
```

```json
{
  "entity": "telefono",
  "database": "MongoDB",
  "count": 5
}
```

### 54. [MONGO] Estudio · count — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/estudio/MONGO/count
```

```json
{
  "entity": "estudio",
  "database": "MongoDB",
  "count": 5
}
```

### 55. [MONGO] Persona · getPhones — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/MONGO/111222333/telefonos
```

```json
[
  {
    "number": "3112223344",
    "company": "WOM",
    "ownerId": "111222333",
    "database": "MongoDB",
    "ownerName": "Laura Gomez Diaz",
    "status": "OK"
  }
]
```

### 56. [MONGO] Persona · getStudies — HTTP 200 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/MONGO/111222333/estudios
```

```json
[
  {
    "professionId": "10",
    "personId": "111222333",
    "graduationDate": "2021-12-10",
    "universityName": "Universidad de Antioquia",
    "database": "MongoDB",
    "professionName": "Arquitecta",
    "personName": "Laura Gomez Diaz",
    "status": "OK"
  }
]
```

### 57. [MONGO] Estudio · drop — HTTP 200 ✅

```bash
curl -s -X DELETE http://localhost:3000/api/v1/estudio/MONGO/10/111222333
```

```json
{
  "status": "OK",
  "description": "estudio (10, 111222333) eliminado de MongoDB",
  "date": "15/11/2026"
}
```

### 58. [MONGO] Teléfono · drop — HTTP 200 ✅

```bash
curl -s -X DELETE http://localhost:3000/api/v1/telefono/MONGO/3112223344
```

```json
{
  "status": "OK",
  "description": "telefono 3112223344 eliminado de MongoDB",
  "date": "15/11/2026"
}
```

### 59. [MONGO] Persona · drop — HTTP 200 ✅

```bash
curl -s -X DELETE http://localhost:3000/api/v1/persona/MONGO/111222333
```

```json
{
  "status": "OK",
  "description": "persona 111222333 eliminada de MongoDB",
  "date": "15/11/2026"
}
```

### 60. [MONGO] Profesión · drop — HTTP 200 ✅

```bash
curl -s -X DELETE http://localhost:3000/api/v1/profesion/MONGO/10
```

```json
{
  "status": "OK",
  "description": "profesion 10 eliminada de MongoDB",
  "date": "15/11/2026"
}
```

### 61. [MONGO] Error 404 · persona inexistente — HTTP 404 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/MONGO/1
```

```json
{
  "status": 404,
  "mensaje": "The person with id 1 does not exist into db, cannot be found",
  "timestamp": "2026-11-15T20:05:39"
}
```

### 62. [MONGO] Error 404 · estudio inexistente — HTTP 404 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/estudio/MONGO/10/111222333
```

```json
{
  "status": 404,
  "mensaje": "The study with profession id 10 and person id 111222333 does not exist into db, cannot be found",
  "timestamp": "2026-11-15T20:05:39"
}
```

### 63. [MONGO] Error 404 · teléfono con dueño inexistente — HTTP 404 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/telefono -H 'Content-Type: application/json' -d '{"number":"3000000000","company":"Claro","ownerId":"999","database":"MONGO"}'
```

```json
{
  "status": 404,
  "mensaje": "The person with id 999 does not exist into db, cannot be found",
  "timestamp": "2026-11-15T20:05:40"
}
```

### 64. [MONGO] Error 404 · editar profesión inexistente — HTTP 404 ✅

```bash
curl -s -X PUT http://localhost:3000/api/v1/profesion/MONGO/99 -H 'Content-Type: application/json' -d '{"name":"X"}'
```

```json
{
  "status": 404,
  "mensaje": "The profession with id 99 does not exist into db, cannot be edited",
  "timestamp": "2026-11-15T20:05:40"
}
```

### 65. [MONGO] Error 400 · sexo inválido — HTTP 400 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/persona -H 'Content-Type: application/json' -d '{"dni":"5","firstName":"A","lastName":"B","sex":"X","database":"MONGO"}'
```

```json
{
  "status": 400,
  "mensaje": "sex must be M or F: X",
  "timestamp": "2026-11-15T20:05:41"
}
```

### 66. [MONGO] Error 400 · fecha inválida — HTTP 400 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/estudio -H 'Content-Type: application/json' -d '{"professionId":"1","personId":"147258369","graduationDate":"30/06/2020","database":"MONGO"}'
```

```json
{
  "status": 400,
  "mensaje": "graduationDate must be a date with format yyyy-MM-dd: 30/06/2020",
  "timestamp": "2026-11-15T20:05:41"
}
```

### 67. [MONGO] Error 409 · cédula duplicada — HTTP 409 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/persona -H 'Content-Type: application/json' -d '{"dni":"123456789","firstName":"Pepe","lastName":"Perez","sex":"M","database":"MONGO"}'
```

```json
{
  "status": 409,
  "mensaje": "The person with id 123456789 already exists into db, cannot be created",
  "timestamp": "2026-11-15T20:05:42"
}
```

### 68. [MONGO] Error 409 · borrar persona con teléfonos — HTTP 409 ✅

```bash
curl -s -X DELETE http://localhost:3000/api/v1/persona/MONGO/123456789
```

```json
{
  "status": 409,
  "mensaje": "The person with id 123456789 has phones or studies, cannot be dropped",
  "timestamp": "2026-11-15T20:05:42"
}
```

## Errores de base de datos

### 69. Error 400 · base inválida (GET) — HTTP 400 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/ORACLE
```

```json
{
  "status": 400,
  "mensaje": "Invalid database option: ORACLE (use MARIA or MONGO)",
  "timestamp": "2026-11-15T20:05:43"
}
```

### 70. Error 400 · base inválida (POST) — HTTP 400 ✅

```bash
curl -s -X POST http://localhost:3000/api/v1/profesion -H 'Content-Type: application/json' -d '{"id":"11","name":"X","database":"POSTGRES"}'
```

```json
{
  "status": 400,
  "mensaje": "Invalid database option: POSTGRES (use MARIA or MONGO)",
  "timestamp": "2026-11-15T20:05:43"
}
```

### 71. Error 400 · id no numérico — HTTP 400 ✅

```bash
curl -s -X GET http://localhost:3000/api/v1/persona/MARIA/abc
```

```json
{
  "status": 400,
  "mensaje": "cc must be a valid Integer: abc",
  "timestamp": "2026-11-15T20:05:43"
}
```

## Resumen

| # | Caso | Petición | Esperado | Obtenido | |
|---|---|---|---|---|---|
| 1 | [MARIA] Profesión · create | `POST /profesion` | 201 | 201 | ✅ |
| 2 | [MARIA] Persona · create | `POST /persona` | 201 | 201 | ✅ |
| 3 | [MARIA] Teléfono · create | `POST /telefono` | 201 | 201 | ✅ |
| 4 | [MARIA] Estudio · create | `POST /estudio` | 201 | 201 | ✅ |
| 5 | [MARIA] Profesión · findAll | `GET /profesion/MARIA` | 200 | 200 | ✅ |
| 6 | [MARIA] Persona · findAll | `GET /persona/maria` | 200 | 200 | ✅ |
| 7 | [MARIA] Teléfono · findAll | `GET /telefono/MARIA` | 200 | 200 | ✅ |
| 8 | [MARIA] Estudio · findAll | `GET /estudio/MARIA` | 200 | 200 | ✅ |
| 9 | [MARIA] Profesión · findOne | `GET /profesion/MARIA/10` | 200 | 200 | ✅ |
| 10 | [MARIA] Persona · findOne | `GET /persona/MARIA/111222333` | 200 | 200 | ✅ |
| 11 | [MARIA] Teléfono · findOne | `GET /telefono/MARIA/3112223344` | 200 | 200 | ✅ |
| 12 | [MARIA] Estudio · findOne | `GET /estudio/MARIA/10/111222333` | 200 | 200 | ✅ |
| 13 | [MARIA] Profesión · edit | `PUT /profesion/MARIA/10` | 200 | 200 | ✅ |
| 14 | [MARIA] Persona · edit | `PUT /persona/MARIA/111222333` | 200 | 200 | ✅ |
| 15 | [MARIA] Teléfono · edit | `PUT /telefono/MARIA/3112223344` | 200 | 200 | ✅ |
| 16 | [MARIA] Estudio · edit | `PUT /estudio/MARIA/10/111222333` | 200 | 200 | ✅ |
| 17 | [MARIA] Profesión · count | `GET /profesion/MARIA/count` | 200 | 200 | ✅ |
| 18 | [MARIA] Persona · count | `GET /persona/MARIA/count` | 200 | 200 | ✅ |
| 19 | [MARIA] Teléfono · count | `GET /telefono/MARIA/count` | 200 | 200 | ✅ |
| 20 | [MARIA] Estudio · count | `GET /estudio/MARIA/count` | 200 | 200 | ✅ |
| 21 | [MARIA] Persona · getPhones | `GET /persona/MARIA/111222333/telefonos` | 200 | 200 | ✅ |
| 22 | [MARIA] Persona · getStudies | `GET /persona/MARIA/111222333/estudios` | 200 | 200 | ✅ |
| 23 | [MARIA] Estudio · drop | `DELETE /estudio/MARIA/10/111222333` | 200 | 200 | ✅ |
| 24 | [MARIA] Teléfono · drop | `DELETE /telefono/MARIA/3112223344` | 200 | 200 | ✅ |
| 25 | [MARIA] Persona · drop | `DELETE /persona/MARIA/111222333` | 200 | 200 | ✅ |
| 26 | [MARIA] Profesión · drop | `DELETE /profesion/MARIA/10` | 200 | 200 | ✅ |
| 27 | [MARIA] Error 404 · persona inexistente | `GET /persona/MARIA/1` | 404 | 404 | ✅ |
| 28 | [MARIA] Error 404 · estudio inexistente | `GET /estudio/MARIA/10/111222333` | 404 | 404 | ✅ |
| 29 | [MARIA] Error 404 · teléfono con dueño inexistente | `POST /telefono` | 404 | 404 | ✅ |
| 30 | [MARIA] Error 404 · editar profesión inexistente | `PUT /profesion/MARIA/99` | 404 | 404 | ✅ |
| 31 | [MARIA] Error 400 · sexo inválido | `POST /persona` | 400 | 400 | ✅ |
| 32 | [MARIA] Error 400 · fecha inválida | `POST /estudio` | 400 | 400 | ✅ |
| 33 | [MARIA] Error 409 · cédula duplicada | `POST /persona` | 409 | 409 | ✅ |
| 34 | [MARIA] Error 409 · borrar persona con teléfonos | `DELETE /persona/MARIA/123456789` | 409 | 409 | ✅ |
| 35 | [MONGO] Profesión · create | `POST /profesion` | 201 | 201 | ✅ |
| 36 | [MONGO] Persona · create | `POST /persona` | 201 | 201 | ✅ |
| 37 | [MONGO] Teléfono · create | `POST /telefono` | 201 | 201 | ✅ |
| 38 | [MONGO] Estudio · create | `POST /estudio` | 201 | 201 | ✅ |
| 39 | [MONGO] Profesión · findAll | `GET /profesion/MONGO` | 200 | 200 | ✅ |
| 40 | [MONGO] Persona · findAll | `GET /persona/mongo` | 200 | 200 | ✅ |
| 41 | [MONGO] Teléfono · findAll | `GET /telefono/MONGO` | 200 | 200 | ✅ |
| 42 | [MONGO] Estudio · findAll | `GET /estudio/MONGO` | 200 | 200 | ✅ |
| 43 | [MONGO] Profesión · findOne | `GET /profesion/MONGO/10` | 200 | 200 | ✅ |
| 44 | [MONGO] Persona · findOne | `GET /persona/MONGO/111222333` | 200 | 200 | ✅ |
| 45 | [MONGO] Teléfono · findOne | `GET /telefono/MONGO/3112223344` | 200 | 200 | ✅ |
| 46 | [MONGO] Estudio · findOne | `GET /estudio/MONGO/10/111222333` | 200 | 200 | ✅ |
| 47 | [MONGO] Profesión · edit | `PUT /profesion/MONGO/10` | 200 | 200 | ✅ |
| 48 | [MONGO] Persona · edit | `PUT /persona/MONGO/111222333` | 200 | 200 | ✅ |
| 49 | [MONGO] Teléfono · edit | `PUT /telefono/MONGO/3112223344` | 200 | 200 | ✅ |
| 50 | [MONGO] Estudio · edit | `PUT /estudio/MONGO/10/111222333` | 200 | 200 | ✅ |
| 51 | [MONGO] Profesión · count | `GET /profesion/MONGO/count` | 200 | 200 | ✅ |
| 52 | [MONGO] Persona · count | `GET /persona/MONGO/count` | 200 | 200 | ✅ |
| 53 | [MONGO] Teléfono · count | `GET /telefono/MONGO/count` | 200 | 200 | ✅ |
| 54 | [MONGO] Estudio · count | `GET /estudio/MONGO/count` | 200 | 200 | ✅ |
| 55 | [MONGO] Persona · getPhones | `GET /persona/MONGO/111222333/telefonos` | 200 | 200 | ✅ |
| 56 | [MONGO] Persona · getStudies | `GET /persona/MONGO/111222333/estudios` | 200 | 200 | ✅ |
| 57 | [MONGO] Estudio · drop | `DELETE /estudio/MONGO/10/111222333` | 200 | 200 | ✅ |
| 58 | [MONGO] Teléfono · drop | `DELETE /telefono/MONGO/3112223344` | 200 | 200 | ✅ |
| 59 | [MONGO] Persona · drop | `DELETE /persona/MONGO/111222333` | 200 | 200 | ✅ |
| 60 | [MONGO] Profesión · drop | `DELETE /profesion/MONGO/10` | 200 | 200 | ✅ |
| 61 | [MONGO] Error 404 · persona inexistente | `GET /persona/MONGO/1` | 404 | 404 | ✅ |
| 62 | [MONGO] Error 404 · estudio inexistente | `GET /estudio/MONGO/10/111222333` | 404 | 404 | ✅ |
| 63 | [MONGO] Error 404 · teléfono con dueño inexistente | `POST /telefono` | 404 | 404 | ✅ |
| 64 | [MONGO] Error 404 · editar profesión inexistente | `PUT /profesion/MONGO/99` | 404 | 404 | ✅ |
| 65 | [MONGO] Error 400 · sexo inválido | `POST /persona` | 400 | 400 | ✅ |
| 66 | [MONGO] Error 400 · fecha inválida | `POST /estudio` | 400 | 400 | ✅ |
| 67 | [MONGO] Error 409 · cédula duplicada | `POST /persona` | 409 | 409 | ✅ |
| 68 | [MONGO] Error 409 · borrar persona con teléfonos | `DELETE /persona/MONGO/123456789` | 409 | 409 | ✅ |
| 69 | Error 400 · base inválida (GET) | `GET /persona/ORACLE` | 400 | 400 | ✅ |
| 70 | Error 400 · base inválida (POST) | `POST /profesion` | 400 | 400 | ✅ |
| 71 | Error 400 · id no numérico | `GET /persona/MARIA/abc` | 400 | 400 | ✅ |

**Total: 71 casos — 71 correctos, 0 fallidos.**
