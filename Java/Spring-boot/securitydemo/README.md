# Security Demo

A Spring Boot 4 demonstration application focused on role-based and authority-based access control. The project shows how to protect HTTP endpoints with Spring Security, expose API documentation with OpenAPI/Swagger, and verify the behavior with MockMvc-based tests.

## Project purpose

This project is intentionally small and instructional. It demonstrates:

- Spring Security authentication with in-memory users
- HTTP Basic authentication and form login
- Role-based and authority-based authorization
- CSRF protection using a cookie-backed token repository
- OpenAPI documentation generation with Swagger UI
- Endpoint-level access control for public, user, author, and admin routes

## Technology stack

- Java 25
- Spring Boot 4.1.2
- Spring Security
- Spring Web MVC
- Spring HATEOAS
- Springdoc OpenAPI UI (springdoc-openapi-starter-webmvc-ui)
- Maven wrapper for dependency management and running the app
- JUnit 5 + Spring Security Test for automated verification

## Project structure

```text
securitydemo/
├── .mvn/                       # Maven wrapper metadata
├── src/
│   ├── main/
│   │   ├── java/com/frapee/securitydemo/
│   │   │   ├── SecuritydemoApplication.java
│   │   │   ├── config/
│   │   │   │   ├── ConfigOpenAPI.java
│   │   │   │   └── ConfigSecurity.java
│   │   │   └── controller/
│   │   │       ├── AdminController.java
│   │   │       ├── ApiController.java
│   │   │       ├── AuthorController.java
│   │   │       └── PublicController.java
│   │   └── resources/
│   └── test/
│       └── java/com/frapee/securitydemo/
│           ├── controller/
│           └── integration/
├── .gitignore
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── target/
```

## Application entry point

The application starts from [src/main/java/com/frapee/securitydemo/SecuritydemoApplication.java](src/main/java/com/frapee/securitydemo/SecuritydemoApplication.java). It is a standard Spring Boot class annotated with `@SpringBootApplication`, which triggers component scanning, configuration loading, and embedded web server startup.

## Security configuration

The security rules are defined in [src/main/java/com/frapee/securitydemo/config/ConfigSecurity.java](src/main/java/com/frapee/securitydemo/config/ConfigSecurity.java).

### In-memory users

The project uses `InMemoryUserDetailsManager` with four predefined users:

| Username | Password | Roles / Authorities | Notes |
|---|---|---|---|
| `bla` | `password` | none | Basic demo account |
| `user` | `password` | `ROLE_USER` | Required for `/api/**` |
| `admin` | `password` | `ROLE_ADMIN` | Required for `/admin/**` |
| `author` | `password` | `ROLE_USER`, `AUTHOR` | Required for `/author/**` |

The `userDetailsService()` method registers all users using `User.withDefaultPasswordEncoder()`.

### Authorization rules

The security filter chain configures these URL rules:

- `/api/**` → requires `ROLE_USER`
- `/admin/**` → requires `ROLE_ADMIN`
- `/author/**` → requires authority `AUTHOR`
- `/v3/**` and `/swagger-ui/**` → authenticated users only
- `/public/**` → allowed to anyone

Administrative controller methods also use JSR-250 method security, enabled
with `@EnableMethodSecurity(jsr250Enabled = true)`. Use the unprefixed role name
in `@RolesAllowed("ADMIN")`; Spring Security's default `ROLE_` prefix matches
the user's `ROLE_ADMIN` authority.

### Security features enabled

- `httpBasic(Customizer.withDefaults())` enables HTTP Basic authentication.
- `formLogin(Customizer.withDefaults())` enables a login form fallback for browser-based access.
- `CookieCsrfTokenRepository.withHttpOnlyFalse()` sets a CSRF token in a cookie that is readable by JavaScript.
- `CsrfTokenRequestAttributeHandler` is configured so the token is handled through request attributes.

## Controllers and routes

The project contains four REST controllers, each mapped to a different protected region.

### Public endpoints

[PublicController.java](src/main/java/com/frapee/securitydemo/controller/PublicController.java)

Base path: `/public`

| Method | Route | Access | Result |
|---|---|---|---|
| GET | `/public` | public | returns `public access works!` |
| POST | `/public` | public | returns `public post works!` |
| PUT | `/public/{id}` | public | returns `public put works!` |
| DELETE | `/public/{id}` | public | responds with `204 No Content` |

### API endpoints

[ApiController.java](src/main/java/com/frapee/securitydemo/controller/ApiController.java)

Base path: `/api`

| Method | Route | Access | Result |
|---|---|---|---|
| GET | `/api` | `ROLE_USER` | returns `api access works!` |
| POST | `/api` | `ROLE_USER` | returns `api post works!` |
| PUT | `/api/{id}` | `ROLE_USER` | returns `api put works!` |
| DELETE | `/api/{id}` | `ROLE_USER` | responds with `204 No Content` |

### Author endpoints

[AuthorController.java](src/main/java/com/frapee/securitydemo/controller/AuthorController.java)

Base path: `/author`

| Method | Route | Access | Result |
|---|---|---|---|
| GET | `/author` | authority `AUTHOR` | returns `Author access works!` |
| POST | `/author` | authority `AUTHOR` | returns `Author post works!` |
| PUT | `/author/{id}` | authority `AUTHOR` | returns `Author put works!` |
| DELETE | `/author/{id}` | authority `AUTHOR` | responds with `204 No Content` |

### Admin endpoints

[AdminController.java](src/main/java/com/frapee/securitydemo/controller/AdminController.java)

Base path: `/admin`

| Method | Route | Access | Result |
|---|---|---|---|
| GET | `/admin` | `ROLE_ADMIN` | returns `admin access works!` |
| POST | `/admin` | `ROLE_ADMIN` | returns `admin post works!` |
| PUT | `/admin/{id}` | `ROLE_ADMIN` | returns `admin put works!` |
| DELETE | `/admin/{id}` | `ROLE_ADMIN` | responds with `204 No Content` |

## OpenAPI / Swagger configuration

The OpenAPI configuration is in [src/main/java/com/frapee/securitydemo/config/ConfigOpenAPI.java](src/main/java/com/frapee/securitydemo/config/ConfigOpenAPI.java).

It declares a Basic Auth scheme named `basicAuth` and applies it globally as a required security scheme for the API definition.

This exposes:

- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Running the application

From the project root:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell or CMD, you can also use the Maven wrapper script:

```powershell
./mvnw.cmd spring-boot:run
```

Then open the app in a browser and use the configured credentials for authentication.

### Common endpoints

- `http://localhost:8080/public` → public endpoint
- `http://localhost:8080/api` → user-only endpoint
- `http://localhost:8080/author` → author authority endpoint
- `http://localhost:8080/admin` → admin-only endpoint
- `http://localhost:8080/logout` → logout
- `http://localhost:8080/v3/api-docs` → OpenAPI schema
- `http://localhost:8080/swagger-ui/index.html` → Swagger UI

## Authentication examples

This project uses browser-based form login or HTTP Basic auth. For example, when hitting a protected route from a browser, Spring Security will challenge the client and prompt for a username and password.

A typical valid credential set:

```text
Username: user
Password: password
```

For admin access:

```text
Username: admin
Password: password
```

For author access:

```text
Username: author
Password: password
```

## Testing

The project includes MockMvc tests for each controller under [src/test/java/com/frapee/securitydemo/controller](src/test/java/com/frapee/securitydemo/controller) and integration-style tests under [src/test/java/com/frapee/securitydemo/integration](src/test/java/com/frapee/securitydemo/integration).

Tests cover:

- successful access for authorized users
- unauthorized access for anonymous users
- expected HTTP status codes and content types
- CSRF requirements for mutation endpoints

Run the test suite with:

```bash
./mvnw test
```

## Notes on design

This is a demonstration app, not a production security implementation. It intentionally keeps authentication in memory for simplicity and is useful for learning the mechanics of:

- Spring Security filter chains
- `UserDetailsService`
- credentials and role/authority mapping
- method-level or request-level authorization rules
- CSRF configuration and API docs support

## Maven configuration

The build is configured in [pom.xml](pom.xml). The core relevant settings are:

- Spring Boot parent: `4.1.2`
- Java version: `25`
- Springdoc OpenAPI UI version: `3.2.0`
- Included starter dependencies:
  - `spring-boot-starter-security`
  - `spring-boot-starter-webmvc`
  - `spring-boot-starter-hateoas`
  - `springdoc-openapi-starter-webmvc-ui`

## Summary

This project provides a compact, easy-to-follow example of Spring Security in a Spring Boot application. It is ideal for learning how to secure different endpoint groups using role and authority rules, add API documentation, and validate the security behavior with automated tests.
