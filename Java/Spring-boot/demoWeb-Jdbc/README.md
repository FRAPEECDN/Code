# Demo Web JDBC

A Spring Boot web application exposing CRUD endpoints for simple records. It
uses Spring MVC for HTTP and Spring JDBC (`JdbcTemplate`) with explicit,
parameterized SQL for PostgreSQL access. It does not use Spring Data JPA,
Hibernate, or another ORM, and it does not configure authentication or
authorization.

## What this project demonstrates

- Building a REST API with Spring MVC
- Writing SQL CRUD operations with `JdbcTemplate`
- Mapping query results to a plain Java row model
- Validating request data
- Generating OpenAPI documentation and serving Swagger UI
- Testing JDBC behavior with H2 and PostgreSQL/Testcontainers

## Technology stack

- Java 25
- Spring Boot 4.1.2
- Spring Web MVC and Spring JDBC
- PostgreSQL 17 for local and integration-test databases
- H2 in PostgreSQL compatibility mode for repository tests
- Springdoc OpenAPI UI 3.1.0
- Jasypt for encrypting database credentials in local application settings
- Maven wrapper
- JUnit 6, Spring Boot test support, MockMvc, and Testcontainers

## Project structure

```text
demoWeb-Jdbc/
├── .github/modernize/java-upgrade/    # Local modernization tooling hooks
├── .mvn/wrapper/                     # Maven wrapper metadata
├── .vscode/
│   ├── launch.json                    # Spring Boot debugger configuration
│   ├── settings.json
│   └── tasks.json
├── docker/postgres/init/01-schema.sql  # Initial PostgreSQL schema
├── scripts/
│   ├── configure-vscode-env.ps1        # Prepare encrypted debugger settings
│   └── run-with-docker-db.ps1          # Start PostgreSQL and the application
├── src/
│   ├── main/
│   │   ├── java/com/fp/boot_demo_web/
│   │   │   ├── BootDemoWebApplication.java
│   │   │   ├── config/
│   │   │   │   ├── DatabaseCredentialEncryptor.java
│   │   │   │   ├── EncryptionConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/SimpleController.java
│   │   │   ├── exceptions/
│   │   │   │   ├── EntityNotFoundException.java
│   │   │   │   ├── ExceptionRestHandler.java
│   │   │   │   └── GeneralServiceException.java
│   │   │   ├── model/dto/SimpleDto.java
│   │   │   ├── model/entities/SimpleEntity.java # Plain JDBC row model
│   │   │   ├── repository/SimpleRepository.java
│   │   │   └── service/SimpleService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── schema.sql               # Schema used by tests
│   └── test/
│       ├── java/com/fp/boot_demo_web/
│       └── resources/                  # H2 and integration-test profiles
├── .gitattributes
├── .gitignore
├── compose.yaml
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

The persistence implementation is in
[`SimpleRepository`](src/main/java/com/fp/boot_demo_web/repository/SimpleRepository.java).
It uses `JdbcTemplate` for SQL and maps result rows explicitly to
[`SimpleEntity`](src/main/java/com/fp/boot_demo_web/model/entities/SimpleEntity.java).

## API

[`SimpleController`](src/main/java/com/fp/boot_demo_web/controller/SimpleController.java)
maps the CRUD API under `/api/simple`.

| Method | Route | Description |
| --- | --- | --- |
| Method | Route | Description | Typical response |
| --- | --- | --- | --- |
| GET | `/api/simple` | List all records | `200 OK` |
| GET | `/api/simple/{id}` | Read one record | `200 OK`, or `404 Not Found` |
| GET | `/api/simple/search?page=0&size=10` | Return a page of records | `200 OK` |
| POST | `/api/simple` | Create a record | `201 Created` |
| PUT | `/api/simple/{id}` | Replace a record | `200 OK`, or `404 Not Found` |
| PATCH | `/api/simple/{id}` | Update a record | `200 OK`, or `404 Not Found` |
| DELETE | `/api/simple/{id}` | Delete a record | `204 No Content`, or `404 Not Found` |

The request and response body uses
[`SimpleDto`](src/main/java/com/fp/boot_demo_web/model/dto/SimpleDto.java), for
example:

```json
{
  "id": 0,
  "name": "apple"
}
```

Names must be non-blank and between 3 and 50 characters. Search pagination
uses a zero-based `page` parameter and defaults to `page=0` and `size=10`.

## OpenAPI and Swagger UI

[`OpenApiConfig`](src/main/java/com/fp/boot_demo_web/config/OpenApiConfig.java)
sets the title and version of the generated API specification.

The OpenAPI JSON is available at
[http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs), and
Swagger UI is at
[http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html).
These endpoints are not protected by authentication.

## Running the application

Requirements: Java 25, Docker with Docker Compose, and PowerShell. A separate
Maven installation is not needed because the Maven wrapper is included.

Set the PostgreSQL credentials and a separate encryption key in the process
environment, then run the launch script from the project root:

```powershell
$env:DB_USERNAME = '<database username>'
$env:DB_PASSWORD = '<database password>'
$env:JASYPT_ENCRYPTOR_PASSWORD = '<separate encryption key>'
.\scripts\run-with-docker-db.ps1
```

The script starts PostgreSQL with Compose, encrypts the database username and
password for Spring, and starts the web application on port `8081`. The
database name defaults to `appdb`; PostgreSQL's published port defaults to
`5432`. Set `DB_NAME` or `POSTGRES_PORT` to override those defaults, and use
`-ServerPort` to change the application port.

On first initialization, PostgreSQL runs
[`01-schema.sql`](docker/postgres/init/01-schema.sql). Its data volume is
persistent; changing initialization credentials does not change an
already-initialized database.

### VS Code debugger

Start VS Code with `JASYPT_ENCRYPTOR_PASSWORD` set in its environment, then
prepare the encrypted debugger settings:

```powershell
.\scripts\configure-vscode-env.ps1
```

Launch **Spring Boot-BootDemoWebApplication<demoWeb-Jdbc>** from Run and
Debug. The script writes encrypted connection settings to the ignored
`.vscode/app.env`; the decryption key is not written to that file.

## Testing

Run unit and JDBC repository tests:

```powershell
.\mvnw.cmd test
```

These tests use H2 in PostgreSQL compatibility mode and initialize the schema
from `src/main/resources/schema.sql`. Run all tests, including PostgreSQL 17
integration tests with Testcontainers, using:

```powershell
.\mvnw.cmd verify
```

Integration tests require a running Docker daemon.

## Credentials and deployment notes

For local development, Docker Compose can read `DB_NAME`, `DB_USERNAME`, and
`DB_PASSWORD` from the ignored root `.env` file or the process environment.
Never commit `.env` or put `JASYPT_ENCRYPTOR_PASSWORD` in it. The `ENC(...)`
values protect saved application configuration only; production deployments
should provide database credentials and the encryption key through a secret
manager. This is a learning demo and does not provide production
authentication or authorization.

## Maven configuration

The build is configured in [pom.xml](pom.xml). The Spring Boot parent manages
dependency versions, and the project targets Java 25. `spring-boot-starter-jdbc`
provides JDBC access; the repository uses parameterized SQL through
`JdbcTemplate` and generated keys for inserts. `spring-data-commons` is used
only for the existing paginated API response type. There are no JPA or ORM
dependencies.
