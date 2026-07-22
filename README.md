# clientes-api-ibk

API reactiva en Java 21 con Spring WebFlux para gestionar clientes, con validación, trazabilidad y persistencia en PostgreSQL (R2DBC).

## Requisitos cubiertos
- `POST /clientes` para crear clientes
- `GET /clientes` para listar clientes
- Validación de campos obligatorios
- DTOs para no exponer campos sensibles en listado
- Trazabilidad asíncrona con payload JSON
- Headers de entrada requeridos para trazabilidad
- Pruebas unitarias y de integración reactiva

## Ejecutar

```bash
mvn test
mvn spring-boot:run
```

## Variables de entorno de base de datos

- `DB_R2DBC_URL` (ejemplo: `r2dbc:postgresql://localhost:5432/clientes_db`)
- `DB_USER`
- `DB_PASSWORD`

## Variables de entorno de Event Hub

- `EVENTHUB_ENABLED` (`true` para enviar a Event Hub, `false` para solo log)
- `EVENTHUB_CONNECTION_STRING`
- `EVENTHUB_NAME`

## Headers requeridos

En `POST /clientes`, `GET /clientes` y `PUT /clientes/{id}`:

- `consumerId`
- `traceparent`
- `deviceType`
- `deviceId`

## Notas
- La persistencia usa `Spring Data R2DBC` con PostgreSQL.
- El script de creación de tabla está en `src/main/resources/schema.sql`.
- La publicación de trazas mantiene log en pod y, con `EVENTHUB_ENABLED=true`, también envía a Event Hub.
