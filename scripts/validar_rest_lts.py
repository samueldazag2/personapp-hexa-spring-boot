"""Smoke real: solo crea y limpia sus propias llaves, contra un REST aislado."""
import json
import sys
import uuid
from urllib.request import Request, urlopen
from urllib.error import HTTPError

base = sys.argv[1] if len(sys.argv) > 1 else "http://localhost:13000"
checks = []


def call(method, path, expected=200, body=None):
    data = json.dumps(body).encode() if body is not None else None
    request = Request(base + path, data=data, method=method,
                      headers={"Content-Type": "application/json"})
    try:
        with urlopen(request, timeout=40) as response:
            code, raw = response.status, response.read().decode()
    except HTTPError as error:
        code, raw = error.code, error.read().decode()
    checks.append({"method": method, "path": path, "expected": expected, "actual": code})
    assert code == expected, (method, path, code, raw)
    try:
        return json.loads(raw)
    except json.JSONDecodeError:
        return raw


try:
    schema = call("GET", "/api-docs")
    assert sum(method in ("get", "post", "put", "delete") for path in schema["paths"].values() for method in path) == 26
    call("GET", "/swagger-ui/index.html")
    for database in ("MARIA", "MONGO"):
        identifier = str(-1 - uuid.uuid4().int % 900000000)
        number = "LTS" + identifier[1:]
        entities = [
            ("persona", identifier, {"dni": identifier, "firstName": "PruebaLTS", "lastName": "Temporal", "age": "25", "sex": "F"}),
            ("profesion", identifier, {"id": identifier, "name": "PruebaLTS", "description": "Temporal"}),
            ("telefono", number, {"number": number, "company": "PruebaLTS", "ownerId": identifier}),
            ("estudio", identifier + "/" + identifier, {"professionId": identifier, "personId": identifier, "graduationDate": "2024-02-29", "universityName": "PruebaLTS"}),
        ]
        created = []
        before = {}
        try:
            for entity, key, body in entities:
                prefix = "/api/v1/" + entity
                path = prefix + "/" + database + "/" + key
                before[entity] = call("GET", prefix + "/" + database + "/count")["count"]
                call("GET", path, 404)
                body["database"] = database
                call("POST", prefix, 201, body)
                created.append(path)
                call("POST", prefix, 409, body)
                result = call("GET", path)
                assert result["database"] == ("MariaDB" if database == "MARIA" else "MongoDB")
                changed = dict(body)
                field = {"persona": "firstName", "profesion": "description", "telefono": "company", "estudio": "universityName"}[entity]
                changed[field] = "ActualizadaLTS"
                call("PUT", path, 200, changed)
                assert call("GET", path)[field] == "ActualizadaLTS"
                assert call("GET", prefix + "/" + database + "/count")["count"] == before[entity] + 1
                call("GET", prefix + "/" + database)
            for relationship in ("telefonos", "estudios"):
                assert len(call("GET", "/api/v1/persona/" + database + "/" + identifier + "/" + relationship)) == 1
            call("DELETE", created[0], 409)
            call("DELETE", created[1], 409)
            call("GET", "/api/v1/persona/" + database + "/no-numero", 400)
            call("POST", "/api/v1/persona", 400, {"database": database})
        finally:
            for path in reversed(created):
                call("DELETE", path)
                call("GET", path, 404)
            for entity, count in before.items():
                assert call("GET", "/api/v1/" + entity + "/" + database + "/count")["count"] == count
    call("GET", "/api/v1/persona/INVALIDA", 400)
    print(json.dumps({"base": base, "passed": len(checks), "checks": checks}, ensure_ascii=False, indent=2))
except Exception:
    print(json.dumps({"base": base, "checks": checks}, indent=2))
    raise
