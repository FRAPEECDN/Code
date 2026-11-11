# TOTP Security Demo

A Spring Boot 4 application demonstrating password plus time-based one-time password (TOTP) multifactor authentication (MFA), role-based access control, CSRF protection, and OpenAPI documentation. The TOTP calculation uses only JDK cryptography; it adds no TOTP library dependency.

This is an instructional project, not production authentication software. Demo users and secrets are held in memory, and the secret is shown on the MFA challenge page to make authenticator setup easy to follow.

## What it demonstrates

- Username/password login followed by a separate TOTP challenge
- RFC 6238 TOTP generation and verification using JDK `Mac` / `HmacSHA1`
- Random, independent 160-bit Base32 demo secrets for each account
- Withholding the user's roles until the TOTP step succeeds
- Role and authority access rules for public, user, author, and admin endpoints
- Session-based authentication and cookie-backed CSRF tokens
- Swagger UI and OpenAPI documentation

## Technology

- Java 25
- Spring Boot 4.1.2 and Spring Security
- Spring Web MVC
- Springdoc OpenAPI UI 3.2.0
- Maven wrapper
- JUnit 5, Spring Security Test, and MockMvc

## Demo accounts

All demo accounts use the password `password`.

| Username | Role / authority | Access after MFA |
|---|---|---|
| `bla` | no application role | Dashboard and authenticated docs |
| `user` | `ROLE_USER` | `/api/**` |
| `admin` | `ROLE_ADMIN` | `/admin/**` |
| `author` | `ROLE_USER`, `AUTHOR` | `/api/**`, `/author/**` |

After password login, the browser is redirected to `/mfa`. Add the displayed Base32 secret to any authenticator app as a time-based (TOTP) account, then enter its six-digit code. The challenge page also displays the corresponding `otpauth://` provisioning URI for authenticator apps that accept manual setup URIs.

Secrets are generated independently when the application starts and are held in memory only. Restarting the app generates new secrets, so set up the demo account again.

## Authentication flow

1. Spring Security verifies the username and password using BCrypt.
2. The success handler stores a session authentication with only `MFA_PENDING`; it does not yet include application roles.
3. `/mfa` shows the account's demo secret and requests its current six-digit TOTP code.
4. On successful verification, the application reloads the account's authorities and upgrades the session to `MFA_COMPLETE`.
5. A pending session cannot access role-protected endpoints. Invalid codes keep the session pending.

The verifier accepts the current 30-second time step and one adjacent step on either side to accommodate minor clock drift. The TOTP implementation and its RFC test vectors are in [MfaService.java](src/main/java/com/frapee/securitydemo/security/MfaService.java).

## Routes

| Method | Route | Access |
|---|---|---|
| GET/POST | `/public` | Public |
| PUT/DELETE | `/public/{id}` | Public |
| GET/POST | `/api` | `ROLE_USER`, after MFA |
| PUT/DELETE | `/api/{id}` | `ROLE_USER`, after MFA |
| GET/POST | `/author` | `AUTHOR`, after MFA |
| PUT/DELETE | `/author/{id}` | `AUTHOR`, after MFA |
| GET/POST | `/admin` | `ROLE_ADMIN`, after MFA |
| PUT/DELETE | `/admin/{id}` | `ROLE_ADMIN`, after MFA |
| GET/POST | `/login` | Public; POST requires CSRF |
| GET | `/mfa` | Password accepted; TOTP pending |
| POST | `/mfa/verify` | Password accepted; TOTP pending; CSRF required |
| GET | `/dashboard` | Both factors verified |
| GET | `/v3/api-docs` | Both factors verified |
| GET | `/swagger-ui/index.html` | Both factors verified |
| POST | `/logout` | Clears the session; CSRF required |

The API controllers preserve the simple request/response behavior of the sibling `securitydemo` project. Mutating requests use CSRF protection.

## Run

From this directory:

```powershell
.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). Select a demo account on the Spring Security login form, then complete the TOTP challenge. Swagger UI is at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) after MFA.

## Test

```powershell
.\mvnw.cmd test
```

The tests cover RFC 6238 SHA-1 test vectors, secret generation, rejected code formats, the pending-to-complete login transition, and post-MFA role and authority enforcement. MVC tests exercise GET, POST, PUT, and DELETE requests for each public and role-protected controller, including CSRF checks. Live HTTP REST tests repeat those calls through the login and TOTP flow and verify role denials.

## Project structure

```text
src/
├── main/java/com/frapee/securitydemo/
│   ├── config/
│   │   ├── ConfigOpenAPI.java
│   │   └── ConfigSecurity.java
│   ├── controller/
│   │   ├── AdminController.java
│   │   ├── ApiController.java
│   │   ├── AuthorController.java
│   │   ├── MfaController.java
│   │   └── PublicController.java
│   └── security/
│       ├── MfaAuthenticationSuccessHandler.java
│       └── MfaService.java
└── test/java/com/frapee/securitydemo/
    ├── MfaControllerMvcTests.java
    ├── MfaLoginFlowTests.java
    ├── RestApiControllerTests.java
    └── security/MfaServiceTests.java
```

## Production limitations

This project is deliberately simplified for security education. It uses in-memory accounts and secrets, displays demo secrets after password login, has no enrollment/recovery workflow or rate limiting, and does not prevent reuse of a valid code during its time window. A real application must securely enroll users, store protected per-user secrets, rate-limit and audit failed attempts, prevent replay, define recovery procedures, and use HTTPS.
