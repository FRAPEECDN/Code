# Spring Boot Demos

This folder contains independent Spring Boot projects targeting Java 25. Each demo focuses on a different web or security topic; the project README has the complete prerequisites and build, run, and test instructions.

| Project | What it demonstrates |
| --- | --- |
| [boot-demo-web](boot-demo-web/README.md) | A validated Spring MVC CRUD API backed by Spring Data JPA and PostgreSQL, with OpenAPI/Swagger documentation and H2/Testcontainers tests. It does not configure authentication. |
| [JwtSecurityDemo](JwtSecurityDemo/README.md) | Stateless bearer-token authentication with a custom JWT filter, in-memory users, role and authority authorization, CSRF protection, and OpenAPI/Swagger. |
| [securitydemo](securitydemo/README.md) | Spring Security with in-memory users, HTTP Basic and form login, role- and authority-based endpoint rules, method security, CSRF, and OpenAPI/Swagger. |
| [securitydemo-totp](securitydemo-totp/README.md) | Password login followed by a TOTP multifactor challenge, with post-MFA role authorization, session security, CSRF protection, and OpenAPI/Swagger. |

These are instructional examples, not production-ready authentication configurations. In particular, the security demos use in-memory demonstration accounts and credentials; consult each project README for its specific behavior and limitations.
