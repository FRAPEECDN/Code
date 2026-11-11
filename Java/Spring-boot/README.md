# Spring Boot Demos

All five projects in this folder use Spring Boot 4 and target Java 25 only; there are no Java 17 or Java 21 variants here. Each project is built independently. Its README has the complete prerequisites and build, run, and test instructions.

| Project | What it demonstrates |
| --- | --- |
| [demoWeb-Jpa](demoWeb-Jpa/README.md) | A validated Spring MVC CRUD API backed by Spring Data JPA and PostgreSQL, with OpenAPI/Swagger documentation and H2/Testcontainers tests. It does not configure authentication. |
| [demoWeb-Jdbc](demoWeb-Jdbc/README.md) | The same validated Spring MVC CRUD API implemented with Spring JDBC and parameterized SQL, without JPA or an ORM. |
| [securityDemo-Jwt](securityDemo-Jwt/README.md) | Stateless bearer-token authentication with a custom JWT filter, in-memory users, role and authority authorization, CSRF protection, and OpenAPI/Swagger. |
| [securityDemo-BasicAuth](securityDemo-BasicAuth/README.md) | Spring Security with in-memory users, HTTP Basic and form login, role- and authority-based endpoint rules, method security, CSRF, and OpenAPI/Swagger. |
| [securityDemo-Totp](securityDemo-Totp/README.md) | Password login followed by a TOTP multifactor challenge, with post-MFA role authorization, session security, CSRF protection, and OpenAPI/Swagger. |

These are instructional examples, not production-ready authentication configurations. In particular, the security demos use in-memory demonstration accounts and credentials; consult each project README for its specific behavior and limitations.
