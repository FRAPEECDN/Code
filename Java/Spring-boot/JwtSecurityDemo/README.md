# JWT Security Demo

A Spring Boot demonstration application for stateless authentication with JSON Web Tokens (JWT). It shows how to authenticate in-memory users, authorize endpoints by role or authority, enforce CSRF protection, expose API documentation with OpenAPI/Swagger, and verify behavior with automated tests.

## Project purpose

This project is intentionally small and instructional. It demonstrates:

- JWT bearer-token authentication through a custom Spring Security filter
- User sign-in with an in-memory user store
- Role-based and authority-based endpoint authorization
- CSRF protection using a cookie-backed token repository
- OpenAPI documentation and Swagger UI
- Controller and integration tests for access rules and HTTP behavior

This is a learning example, not a production-ready security configuration.

## Technology stack

- Java 25
- Spring Boot 4.1.2
- Spring Security
- Spring Web
- JJWT 0.13.0
- Springdoc OpenAPI UI 3.2.0
- Maven wrapper
- JUnit 5, Spring Security Test, and Spring MVC test support

## Project structure

```text
JwtSecurityDemo/
├── .mvn/                                      # Maven wrapper metadata
├── src/
│   ├── main/
│   │   ├── java/com/frapee/JwtSecurityDemo/
│   │   │   ├── JwtSecurityDemoApplication.java
│   │   │   ├── config/
│   │   │   │   ├── ConfigOpenApi.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controllers/
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── ApiController.java
│   │   │   │   ├── AuthorController.java
│   │   │   │   ├── JwtController.java
│   │   │   │   └── PublicController.java
│   │   │   └── jwt/
│   │   │       ├── JwtAuthenticationFilter.java
│   │   │       ├── JwtResponse.java
│   │   │       ├── JwtService.java
│   │   │       └── LoginRequest.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/frapee/JwtSecurityDemo/
│           ├── controllers/                   # MVC controller tests
│           └── integration/                   # Security and HTTP integration tests
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Application entry point

The application starts from [JwtSecurityDemoApplication.java](src/main/java/com/frapee/JwtSecurityDemo/JwtSecurityDemoApplication.java). Its `@SpringBootApplication` annotation enables component scanning, configuration, and the embedded web server.

## Authentication and authorization

`/signin` authenticates a username and password and returns a JWT. Subsequent requests to protected endpoints use the token in an `Authorization: Bearer <token>` header. `JwtAuthenticationFilter` validates the token and loads the user's authorities from the in-memory user store.

### Demo users

| Username | Password | Roles / authorities | Access |
| --- | --- | --- | --- |
| `bla` | `password` | none | Public routes only |
| `user` | `password` | `ROLE_USER` | `/api/**` |
| `admin` | `password` | `ROLE_ADMIN` | `/admin/**` |
| `author` | `password` | `AUTHOR` | `/author/**` |

These credentials are for local demonstration only.

### Security configuration

The filter chain and in-memory users are configured in [SecurityConfig.java](src/main/java/com/frapee/JwtSecurityDemo/config/SecurityConfig.java).

| URL pattern | Access rule |
| --- | --- |
| `/signin` | Public |
| `/v3/**`, `/swagger-ui/**` | Public |
| `/public/**` | Public |
| `/api/**` | Requires `ROLE_USER` |
| `/admin/**` | Requires `ROLE_ADMIN` |
| `/author/**` | Requires authority `AUTHOR` |

The application uses stateless sessions; HTTP Basic and form login are not configured. CSRF protection remains enabled with `CookieCsrfTokenRepository`; include a valid CSRF token for unsafe methods such as `POST`, `PUT`, and `DELETE`, including `/signin`. The token must be sent in the configured CSRF header and matching `XSRF-TOKEN` cookie. The automated integration tests create and send both values.

## Controllers and routes

The controllers provide demo responses and do not persist or modify business data.

| Controller | Route | Access |
| --- | --- | --- |
| [PublicController.java](src/main/java/com/frapee/JwtSecurityDemo/controllers/PublicController.java) | `/public` and `/public/{id}` | Public |
| [ApiController.java](src/main/java/com/frapee/JwtSecurityDemo/controllers/ApiController.java) | `/api` and `/api/{id}` | `ROLE_USER` |
| [AuthorController.java](src/main/java/com/frapee/JwtSecurityDemo/controllers/AuthorController.java) | `/author` and `/author/{id}` | Authority `AUTHOR` |
| [AdminController.java](src/main/java/com/frapee/JwtSecurityDemo/controllers/AdminController.java) | `/admin` and `/admin/{id}` | `ROLE_ADMIN` |
| [JwtController.java](src/main/java/com/frapee/JwtSecurityDemo/controllers/JwtController.java) | `POST /signin` | Public, requires CSRF token |

Each resource controller supports `GET` and `POST` on its base path and `PUT` and `DELETE` on `/{id}`. `GET` and `PUT` return `200 OK`, `POST` returns `201 Created`, and `DELETE` returns `204 No Content`.

## Sign-in and API request example

Send a JSON request to `POST /signin`:

```json
{
  "name": "user",
  "password": "password"
}
```

The response contains a bearer token and username:

```json
{
  "token": "<jwt-token>",
  "user": "user",
  "type": "Bearer"
}
```

For a protected route, include the returned token:

```bash
curl http://localhost:8080/api \
  -H "Authorization: Bearer <jwt-token>"
```

Because CSRF protection is enabled, include a valid CSRF token in both the configured request header and the `XSRF-TOKEN` cookie for `POST /signin` and other state-changing requests. Replace the placeholders below with the token issued to your client:

```bash
curl -X POST http://localhost:8080/signin \
  -H "Content-Type: application/json" \
  -H "X-CSRF-TOKEN: <csrf-token>" \
  -b "XSRF-TOKEN=<csrf-token>" \
  -d '{"name":"user","password":"password"}'
```

Use the configured role or authority for the selected route: the `admin` account accesses `/admin`, and the `author` account accesses `/author`.

## OpenAPI and Swagger

OpenAPI metadata and the bearer authentication scheme are configured in [ConfigOpenApi.java](src/main/java/com/frapee/JwtSecurityDemo/config/ConfigOpenApi.java).

- OpenAPI JSON: <http://localhost:8080/v3/api-docs>
- Swagger UI: <http://localhost:8080/swagger-ui/index.html>

The OpenAPI endpoints are public. Use the Swagger **Authorize** control to supply a bearer token when trying protected operations.

## Configuration

JWT settings are defined in [application.yml](src/main/resources/application.yml):

```yaml
security:
  jwt:
    secret-key: <base64-encoded-signing-key>
    expiration-time: 3600000
```

The expiration time is in milliseconds (one hour by default). The checked-in key is for demonstration only. Configure a strong, private signing key through an externalized configuration source or secret manager before deploying the application; do not reuse the sample key.

## Running the application

Prerequisite: install a Java 25 JDK. Run the Maven wrapper from the project root:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell or Command Prompt:

```powershell
.\mvnw.cmd spring-boot:run
```

The application listens on port `8080` by default. Common URLs:

- <http://localhost:8080/public>
- <http://localhost:8080/api>
- <http://localhost:8080/author>
- <http://localhost:8080/admin>
- <http://localhost:8080/v3/api-docs>
- <http://localhost:8080/swagger-ui/index.html>

## Testing

The project includes MVC tests and integration tests for controller behavior, role and authority enforcement, CSRF requirements, and HTTP response status codes.

Run the test suite with:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

## Maven configuration

Build and dependency configuration is in [pom.xml](pom.xml). The project uses Spring Boot 4.1.2, Java 25, Springdoc OpenAPI UI 3.2.0, JJWT 0.13.0, and the Maven wrapper.
