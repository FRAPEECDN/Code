# Boot Demo Web

A small Spring Boot web application that exposes CRUD endpoints for simple
records and generates OpenAPI documentation with Swagger UI. The application
uses Spring MVC and PostgreSQL; it does not configure authentication or
authorization.

## Project purpose

This project demonstrates:

- Building a REST API with Spring MVC
- Creating, reading, updating, and deleting records with Spring Data JPA
- Validating request data
- Generating OpenAPI documentation and serving Swagger UI
- Testing web and persistence behavior with MockMvc and PostgreSQL

## Technology stack

- Java 25
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA and Hibernate
- PostgreSQL 17 for local and integration-test databases
- H2 in PostgreSQL compatibility mode for unit and repository tests
- Springdoc OpenAPI UI 3.1.0
- Jasypt for encrypting database credentials in local application settings
- Maven wrapper
- JUnit 6, Spring Boot test support, MockMvc, and Testcontainers

## Project structure

```text
boot-demo-web/
├── .mvn/                              # Maven wrapper metadata
├── docker/postgres/init/
│   └── 01-schema.sql                  # Initial PostgreSQL schema
├── scripts/
│   ├── configure-vscode-env.ps1       # Prepare encrypted debugger settings
│   └── run-with-docker-db.ps1         # Start PostgreSQL and the application
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
│   │   │   ├── model/
│   │   │   │   ├── dto/SimpleDto.java
│   │   │   │   └── entities/SimpleEntity.java
│   │   │   ├── repository/SimpleRepository.java
│   │   │   └── service/SimpleService.java
│   │   └── resources/application.yml
│   └── test/
│       ├── java/com/fp/boot_demo_web/ # Unit and integration tests
│       └── resources/                 # H2 and integration-test profiles
├── compose.yaml
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Application entry point

The application starts from
[BootDemoWebApplication.java](src/main/java/com/fp/boot_demo_web/BootDemoWebApplication.java).
Its `@SpringBootApplication` annotation enables Spring Boot auto-configuration
and component scanning.

## Controllers and routes

[`SimpleController`](src/main/java/com/fp/boot_demo_web/controller/SimpleController.java)
maps the CRUD API under `/api/simple`.

| Method | Route | Description | Typical response |
| --- | --- | --- | --- |
| GET | `/api/simple` | List all records | `200 OK` |
| GET | `/api/simple/{id}` | Read one record | `200 OK`, or `404 Not Found` |
| GET | `/api/simple/search?page=0&size=10` | Return a page of records | `200 OK` |
| POST | `/api/simple` | Create a record | `201 Created` |
| PUT | `/api/simple/{id}` | Replace a record | `200 OK`, or `404 Not Found` |
| PATCH | `/api/simple/{id}` | Update a record | `200 OK`, or `404 Not Found` |
| DELETE | `/api/simple/{id}` | Delete a record | `204 No Content`, or `404 Not Found` |

The request and response body uses `SimpleDto`, for example:

```json
{
  "id": 0,
  "name": "apple"
}
```

The name must be non-blank and between 3 and 50 characters. Search pagination
uses a zero-based `page` parameter and defaults to `page=0` and `size=10`.

## OpenAPI and Swagger UI

[`OpenApiConfig`](src/main/java/com/fp/boot_demo_web/config/OpenApiConfig.java)
sets the title and version of the generated API specification. The controller
also provides operation descriptions and request/response metadata.

When the application is running on its default port:

- OpenAPI JSON: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)
- Swagger UI: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

These endpoints are not protected by authentication; the application does not
include Spring Security.

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

On first initialization, the PostgreSQL container creates the database and
runs [`01-schema.sql`](docker/postgres/init/01-schema.sql). Its data volume is
persistent; changing the initialization credentials does not change an
already-initialized database. Hibernate validates the schema on application
startup and does not modify it.

### VS Code debugger

Start VS Code with `JASYPT_ENCRYPTOR_PASSWORD` set in its environment, then
prepare the encrypted debugger settings:

```powershell
.\scripts\configure-vscode-env.ps1
```

Launch **Spring Boot-BootDemoWebApplication<boot-demo-web>** from Run and
Debug. The script writes encrypted connection settings to the ignored
`.vscode/app.env`; the decryption key is not written to that file.

## Testing

Run unit and repository tests:

```powershell
.\mvnw.cmd test
```

These tests use H2 in PostgreSQL compatibility mode. Run all tests, including
PostgreSQL 17 integration tests with Testcontainers, using:

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
Spring dependency versions; the project targets Java 25. `mvnw` and `mvnw.cmd`
run Maven on Unix-like systems and Windows, respectively.

## Summary

Boot Demo Web is an instructional plain web application: it exposes a small
validated CRUD API, persists records with PostgreSQL, and makes its OpenAPI
definition and Swagger UI available to users.
