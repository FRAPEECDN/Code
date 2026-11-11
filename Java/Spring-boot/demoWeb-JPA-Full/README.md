# Demo Web JPA Full

A Spring Boot 4 / Java 25 demo that extends the basic JPA CRUD application with
several related entities and repositories. Goal 1 focuses on persistence
mapping: scalar field types, enum storage, and common table relationships.
The existing `/api/simple` CRUD example remains available alongside student,
department, and course APIs.

## What this project demonstrates

- `Integer`, `Double`, and `Boolean` entity fields
- Enums persisted as a string name and as an ordinal number
- One-to-one, one-to-many / many-to-one, and many-to-many mappings
- Spring Data JPA repositories and derived queries across relationships
- DTOs for the academic entities, with relationship references represented as IDs
- PostgreSQL schema validation and H2 / PostgreSQL integration testing

## Entity model

| Entity | Example fields | Relationships |
| --- | --- | --- |
| `DepartmentEntity` | `name`, `DepartmentType type` (`STRING`) | One department has many courses |
| `CourseEntity` | `Double tuition`, `Boolean active`, `CourseLevel level` (`ORDINAL`) | Many-to-one department; many-to-many students |
| `StudentEntity` | `Integer academicYear`, `Boolean active` | One-to-one profile; many-to-many courses |
| `StudentProfileEntity` | `email`, `phone` | Owning side of the one-to-one student relationship |

`CourseLevel` is stored as its ordinal number; changing the enum declaration
order can change the meaning of existing rows. `DepartmentType` is stored by
its enum name. The many-to-many student/course relationship uses a regular
join table; there are no embedded IDs or separate join entities.

Repositories include relationship queries such as courses by department ID,
students by course ID, and a profile by student ID. Academic endpoints use
DTOs whose related entities are represented by IDs rather than nested DTOs,
avoiding recursive object graphs.

## Transactions and caching scope

`StudentService` provides student CRUD, current course registration and
deregistration, and student-profile CRUD. `DepartmentService` provides
department CRUD and course CRUD, including moving a course between departments.
Registrations use the existing many-to-many link and represent current courses
only; prior academic-year enrollment history is not stored. A course cannot be
deleted while students are registered, and a department cannot be deleted while
it owns courses.

Service reads are read-only transactions; mutations are transactional. Tests
verify that invalid updates roll back and that changes made through both
services can be rolled back atomically. A second-level cache provider is not
configured.

## Technology

- Java 25
- Spring Boot 4.1.2
- Spring Web MVC, Spring Data JPA, and Hibernate
- PostgreSQL 17 for local use and integration tests
- H2 in PostgreSQL compatibility mode for unit and repository tests
- Springdoc OpenAPI UI 3.1.0
- Maven wrapper

## Project layout

```text
demoWeb-JPA-Full/
├── .mvn/wrapper/                         # Maven wrapper metadata
├── .vscode/                              # Debugger and task configuration
├── docker/postgres/init/01-schema.sql    # Tables for all demo entities
├── scripts/                              # PostgreSQL and debugger helpers
├── src/main/java/com/fp/boot_demo_web/
│   ├── controller/                       # Simple, student, department, and course APIs
│   ├── model/dto/                          # Simple and academic API DTOs
│   ├── model/entities/                    # Simple, department, course, student, profile
│   ├── model/enums/                       # CourseLevel and DepartmentType
│   ├── repository/                        # One Spring Data repository per entity
│   └── service/                           # Simple, student, and department services
├── src/main/resources/application.yml
├── src/test/                              # Unit, repository, MVC, and integration tests
├── compose.yaml
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## HTTP APIs

API operation responses return JSON except the explicitly empty `204` responses. OpenAPI
describes the request parameters, DTO schemas, and responses at
<http://localhost:8081/v3/api-docs>; Swagger UI is at
<http://localhost:8081/swagger-ui/index.html>.

### Simple records

The simple-record API is mapped under `/api/simple`.

| Method and route | Success response | Other documented responses |
| --- | --- | --- |
| `GET /api/simple` | `200` — array of `SimpleDto` | `500` — unexpected server error |
| `GET /api/simple/{id}` | `200` — `SimpleDto` | `400` — invalid ID syntax; `404` — record not found; `500` |
| `GET /api/simple/search?page=0&size=10` | `200` — Spring `Page<SimpleDto>` (`content`, page metadata and total count) | `400` — `page` below 0 or `size` below 1; `500` |
| `POST /api/simple` | `201` — created `SimpleDto` | `400` — malformed/invalid body; `409` — positive ID supplied; `500` |
| `PUT /api/simple/{id}` | `200` — updated `SimpleDto` | `400` — malformed/invalid input; `404` — record not found; `409` — nonzero body ID differs from path; `500` |
| `PATCH /api/simple/{id}` | `200` — updated `SimpleDto` | `400` — malformed/invalid name; `404` — record not found; `500` |
| `DELETE /api/simple/{id}` | `204` — empty response | `400` — invalid ID syntax; `404` — record not found; `500` |

`SimpleDto` names must be nonblank and 3–50 characters. The database generates
the identifier: POST and PUT bodies may omit `id`, set it to `null` or `0`, or
(for PUT only) set it to the path ID. PATCH changes only `name` and takes a
body such as `{"name":"pear"}`.

### Students, registrations, and profiles

| Method | Route | Description |
| --- | --- | --- |
| `GET /api/students` | `200` — array of `StudentDto`; `500` |
| `GET /api/students/{studentId}` | `200` — `StudentDto`; `400` — invalid path syntax; `404` — student missing; `409` — nonpositive ID; `500` |
| `POST /api/students` | `201` — created `StudentDto`; `400` — malformed/invalid body or invalid course IDs; `404` — requested course missing; `409` — generated ID/profile supplied; `500` |
| `PUT /api/students/{studentId}` | `200` — updated `StudentDto`; `400` — malformed/invalid input or course IDs; `404` — student or requested course missing; `409` — ID conflict or profile change attempted here; `500` |
| `DELETE /api/students/{studentId}` | `204` — empty response; `400` — invalid path syntax; `404` — student missing; `409` — nonpositive ID; `500` |
| `POST /api/students/{studentId}/courses/{courseId}` | `200` — updated `StudentDto`; `400` — invalid path syntax; `404` — student or course missing; `409` — nonpositive ID; `500` |
| `DELETE /api/students/{studentId}/courses/{courseId}` | `200` — updated `StudentDto`; `400` — invalid path syntax; `404` — student or course missing; `409` — nonpositive ID; `500` |
| `GET /api/students/{studentId}/profile` | `200` — `StudentProfileDto`; `400` — invalid path syntax; `404` — student or profile missing; `409` — nonpositive ID; `500` |
| `POST /api/students/{studentId}/profile` | `201` — created `StudentProfileDto`; `400` — malformed/invalid body; `404` — student missing; `409` — body/path student IDs differ or profile already exists; `500` |
| `PUT /api/students/{studentId}/profile` | `200` — updated `StudentProfileDto`; `400` — malformed/invalid body; `404` — student or profile missing; `409` — body/path student IDs differ; `500` |
| `DELETE /api/students/{studentId}/profile` | `204` — empty response; `400` — invalid path syntax; `404` — student or profile missing; `409` — nonpositive ID; `500` |

### Departments and courses

| Method | Route | Description |
| --- | --- | --- |
| `GET /api/departments` | `200` — array of `DepartmentDto`; `500` |
| `GET /api/departments/{departmentId}` | `200` — `DepartmentDto`; `400` — invalid path syntax; `404` — department missing; `409` — nonpositive ID; `500` |
| `POST /api/departments` | `201` — created `DepartmentDto`; `400` — malformed/invalid body or course IDs; `409` — generated ID supplied or `courseIds` not empty; `500` |
| `PUT /api/departments/{departmentId}` | `200` — updated `DepartmentDto`; `400` — malformed/invalid body or course IDs; `404` — department missing; `409` — ID conflict or course IDs changed in this operation; `500` |
| `DELETE /api/departments/{departmentId}` | `204` — empty response; `400` — invalid path syntax; `404` — department missing; `409` — department has courses or ID is nonpositive; `500` |
| `GET /api/departments/{departmentId}/courses` | `200` — array of `CourseDto`; `400` — invalid path syntax; `404` — department missing; `409` — nonpositive ID; `500` |
| `POST /api/departments/{departmentId}/courses` | `201` — created `CourseDto`; `400` — malformed/invalid body or student IDs; `404` — department missing; `409` — body/path department mismatch, generated ID supplied, or `studentIds` not empty; `500` |
| `GET /api/departments/{departmentId}/courses/{courseId}` | `200` — `CourseDto`; `400` — invalid path syntax; `404` — department/course missing or course belongs elsewhere; `409` — nonpositive ID; `500` |
| `PUT /api/departments/{departmentId}/courses/{courseId}` | `200` — updated `CourseDto`; `400` — malformed/invalid body or student IDs; `404` — current/target department or course missing, or course belongs elsewhere; `409` — ID conflict or registration set changed in this operation; `500` |
| `DELETE /api/departments/{departmentId}/courses/{courseId}` | `204` — empty response; `400` — invalid path syntax; `404` — department/course missing or course belongs elsewhere; `409` — course has registered students or an ID is nonpositive; `500` |

DTO validation failures, malformed JSON, and invalid integer path/query
parameters return `400` with Spring `ProblemDetail`. Missing resources return
`404` with the JSON string `"Request resource not found"`. Service-level
conflicts return `409` with `"Internal conflict happened."`. The simple
service's mapped internal failure returns `500` with `"Server error"`; other
unexpected server errors use Spring Boot's error response. `204` responses
have no response body. The application does not configure authentication or
authorization.

## Running the application

Requirements: Java 25, Docker with Docker Compose, and PowerShell. A separate
Maven installation is not needed because the Maven wrapper is included.

Set the PostgreSQL credentials and a separate encryption key in the process
environment, then run from the project root:

```powershell
$env:DB_USERNAME = '<database username>'
$env:DB_PASSWORD = '<database password>'
$env:JASYPT_ENCRYPTOR_PASSWORD = '<separate encryption key>'
.\scripts\run-with-docker-db.ps1
```

The script starts PostgreSQL and the application on port `8081`. The database
name defaults to `appdb`, and the published PostgreSQL port defaults to
`5432`; use `DB_NAME` or `POSTGRES_PORT` to override them.

On first initialization, Docker runs
[`01-schema.sql`](docker/postgres/init/01-schema.sql). The dedicated volume
for this project is persistent. If upgrading an existing database, create the
new tables before starting with `JPA_DDL_AUTO=validate`; the initialization
script only runs when PostgreSQL first creates its data directory.

### VS Code

Start VS Code with `JASYPT_ENCRYPTOR_PASSWORD` set, then prepare the debugger
environment:

```powershell
.\scripts\configure-vscode-env.ps1
```

Launch **Spring Boot-DemoWebJpaApplication<demoWeb-JPA-Full>** from Run and
Debug. Encrypted database settings are written to the ignored
`.vscode/app.env`; the encryption key is not written to that file.

## Tests

Run unit, MVC-slice, validation, and H2 repository tests:

```powershell
.\mvnw.cmd test
```

Academic repository and service tests cover CRUD operations and relationship
queries for departments, courses, students, and profiles. Transaction tests
flush related writes, roll them back, and verify that no partial state remains.

Run all tests, including PostgreSQL integration tests with Testcontainers:

```powershell
.\mvnw.cmd verify
```

Integration tests require a running Docker daemon.

## Credentials and deployment notes

Docker Compose reads `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD` from the
process environment or an ignored root `.env` file. Never commit `.env` or
place `JASYPT_ENCRYPTOR_PASSWORD` in it. The `ENC(...)` values protect saved
application configuration only; production deployments should provide
credentials and the encryption key through a secret manager.
