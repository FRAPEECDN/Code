# Demo Web Mongo

A Spring Boot 4 web application exposing a validated CRUD API for simple
records. It persists each record as a MongoDB document and publishes OpenAPI
documentation with Swagger UI. This demo targets Java 25 only and does not
include Spring Security.

## What this project demonstrates

- REST CRUD endpoints with Spring Web MVC
- MongoDB persistence with Spring Data MongoDB repositories
- String API identifiers mapped to MongoDB ObjectIds
- Request validation and consistent API error responses
- OpenAPI documentation and Swagger UI
- MockMvc and persistence integration tests against an in-memory MongoDB server

## Technology

- Java 25
- Spring Boot 4.1.2
- Spring Web MVC and Spring Data MongoDB
- MongoDB, using the existing Docker setup in
  [foundation-mongodb](../../Foundation/foundation-mongodb/README.md)
- Springdoc OpenAPI UI 3.1.0
- Maven wrapper

## MongoDB configuration

The application reads the MongoDB connection from these environment variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `MONGODB_URI` | `mongodb://localhost:27017` | MongoDB server URI |
| `MONGODB_DATABASE` | `demo_web_mongo` | Database used by this demo |
| `SERVER_PORT` | `8081` | HTTP port |

Spring Boot 4 maps these values to `spring.mongodb.uri` and
`spring.mongodb.database`. The app uses a dedicated `demo_web_mongo` database;
MongoDB creates it and the `simple` collection on the first insert. Its
configuration is separate from the `foundation` database used by the
Foundation MongoDB sample.

Start the Foundation MongoDB Docker service from its project folder, or use
your existing MongoDB container, then start this application from the project
root:

```powershell
.\mvnw.cmd spring-boot:run
```

The default URI connects to MongoDB published on `localhost:27017`. Override
it when your container uses a different address or authentication:

```powershell
$env:MONGODB_URI = 'mongodb://localhost:27017'
$env:MONGODB_DATABASE = 'demo_web_mongo'
.\mvnw.cmd spring-boot:run
```

Never put credentials in source control. For authenticated MongoDB, provide
the URI through the environment and URL-encode special characters as required
by the MongoDB URI format.

## API

All CRUD routes use `/api/simple`.

| Method | Route | Description | Response |
| --- | --- | --- | --- |
| GET | `/api/simple` | List records | `200 OK` |
| GET | `/api/simple/{id}` | Read a record by MongoDB ObjectId | `200 OK`, or `404 Not Found` |
| GET | `/api/simple/search?page=0&size=10` | Return a page of records | `200 OK` |
| POST | `/api/simple` | Create a record | `201 Created` |
| PUT | `/api/simple/{id}` | Replace a record | `200 OK`, or `404 Not Found` |
| PATCH | `/api/simple/{id}` | Update a record | `200 OK`, or `404 Not Found` |
| DELETE | `/api/simple/{id}` | Delete a record | `204 No Content`, or `404 Not Found` |

A create request omits the database-generated ID:

```json
{
  "id": null,
  "name": "apple"
}
```

MongoDB stores the generated identifier in the document's `_id` field. The
API exposes that ObjectId as a 24-character hexadecimal string. The name must
be non-blank and contain 3 to 50 characters. A client-supplied ID on create is
ignored. Search pagination uses a zero-based `page` parameter, defaulting to
`page=0` and `size=10`.

## OpenAPI and Swagger UI

When the application is running on its default port:

- OpenAPI JSON: <http://localhost:8081/v3/api-docs>
- Swagger UI: <http://localhost:8081/swagger-ui/index.html>

The API is not protected by authentication.

## VS Code

The project includes a Java launch configuration for
`com.fp.boot_demo_web.BootDemoWebApplication`. It uses the same environment
variables and defaults as the Maven launch command. Set `MONGODB_URI` or
`MONGODB_DATABASE` in the VS Code process environment to override them.

## Tests

Run the unit, MVC, and MongoDB persistence integration tests:

```powershell
.\mvnw.cmd test
```

The tests start an in-memory MongoDB wire-protocol server and do not require
Docker or modify the data in your running MongoDB container.

## Project layout

```text
demoWeb-mongo/
├── .mvn/wrapper/                         # Maven wrapper metadata
├── .vscode/launch.json                   # Java debugger launch configuration
├── src/main/java/com/fp/boot_demo_web/
│   ├── config/                           # OpenAPI configuration
│   ├── controller/SimpleController.java  # REST endpoints
│   ├── exceptions/                       # API exception mapping
│   ├── model/dto/SimpleDto.java          # Validated API model
│   ├── model/entities/SimpleEntity.java  # MongoDB document mapping
│   ├── repository/SimpleRepository.java  # Spring Data MongoDB repository
│   └── service/SimpleService.java        # CRUD and DTO conversion
├── src/main/resources/application.yml    # MongoDB and server settings
├── src/test/                             # Unit, MVC, and MongoDB tests
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```
