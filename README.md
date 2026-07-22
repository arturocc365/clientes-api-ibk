# Clientes API — IBK

API reactiva para gestión de clientes construida con **Spring WebFlux**, **R2DBC + PostgreSQL** y trazabilidad asíncrona hacia **Azure Event Hubs**.

## Tecnologías

| Capa | Tecnología |
|---|---|
| Framework | Spring Boot 3.4 + WebFlux |
| Base de datos | PostgreSQL 16 (R2DBC) |
| Trazabilidad | Azure Event Hubs (fallback a log) |
| Documentación | Springdoc OpenAPI 3 |
| Lenguaje | Java 21 |
| Build | Maven |

## Endpoints

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| `POST` | `/clientes` | Crear cliente | `201 Created` |
| `GET` | `/clientes` | Listar clientes activos | `200 OK` |
| `GET` | `/clientes/{id}` | Obtener cliente por ID | `200 OK` |
| `PUT` | `/clientes/{id}` | Actualizar cliente | `200 OK` |
| `DELETE` | `/clientes/{id}` | Desactivar cliente (soft delete) | `204 No Content` |

### Headers requeridos

| Header | Requerido | Descripción |
|---|---|---|
| `consumerId` | ✅ | Identificador del sistema consumidor |
| `traceparent` | ❌ | W3C Trace Context |
| `deviceType` | ❌ | Tipo de dispositivo (`IOS`, `ANDROID`, `WEB`) |
| `deviceId` | ❌ | Identificador del dispositivo |

### Ejemplo — Crear cliente

```bash
curl -X POST http://localhost:8080/clientes \
  -H "Content-Type: application/json" \
  -H "consumerId: SMP" \
  -H "deviceType: WEB" \
  -d '{"nombre":"Juan","apellidoPaterno":"Perez","apellidoMaterno":"Lopez","activo":true}'
```

### Ejemplo — Respuesta de error

```json
{
  "code": "4000",
  "message": "El nombre no puede ser nulo ni vacío",
  "timestamp": "2025-01-01T00:00:00Z"
}
```

| Código | Situación |
|---|---|
| `4000` | Error de validación / request inválido |
| `9999` | Cliente no encontrado |
| `5000` | Error interno del servidor |

## Ejecutar con Docker Compose

```bash
# Compilar el JAR
mvn clean package -DskipTests

# Levantar PostgreSQL + aplicación
docker compose up --build
```

La API estará disponible en `http://localhost:8080`.

La documentación Swagger estará en `http://localhost:8080/swagger-ui.html`.

## Ejecutar en local (sin Docker)

**Requisitos:** Java 21, Maven, PostgreSQL 16 corriendo en `localhost:5432`.

```bash
# Crear base de datos
psql -U postgres -c "CREATE DATABASE clientes_db;"

# Ejecutar la aplicación
mvn spring-boot:run
```

## Variables de entorno

| Variable | Por defecto | Descripción |
|---|---|---|
| `DB_R2DBC_URL` | `r2dbc:postgresql://localhost:5432/clientes_db` | URL R2DBC de PostgreSQL |
| `DB_USER` | `postgres` | Usuario de base de datos |
| `DB_PASSWORD` | `postgres` | Contraseña de base de datos |
| `EVENTHUB_CONNECTION_STRING` | _(vacío)_ | Connection string de Azure Event Hubs. Si no se configura, los eventos se registran en el log. |
| `EVENTHUB_NAME` | `clientes-trace` | Nombre del Event Hub |

## Trazabilidad

Cada operación publica un evento de analítica con la siguiente estructura:

```json
{
  "analyticsTraceSource": "application-SMP",
  "consumerId": "SMP",
  "customerId": "uuid-del-cliente",
  "region": "este2",
  "statusCode": "0000",
  "transactionCode": "102",
  "inbound": "{ ... request ... }",
  "outbound": "{ ... response ... }",
  "traceId": "db65adadcc7ab67b6eaa38521c34c42a"
}
```

| Operación | `transactionCode` |
|---|---|
| Crear cliente | `102` |
| Consulta (list / get) | `002` |
| Eliminar cliente | `202` |

## Tests

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 mvn test
```
