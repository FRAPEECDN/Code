# Foundation JDBC

A learning project for plain JDBC, project lifecycle persistence, PostgreSQL connection pooling, and encrypted database credentials. The same application source is compiled with Java 17, 21, and 25. No ORM is used.

## Project Layout

| Path | Purpose |
| --- | --- |
| `app-java17/src/main/java` | Shared application, domain model, and repository implementations |
| `app-java17/src/main/resources/db-config.yml` | PostgreSQL URLs and encrypted username/password values |
| `app-java17/src/test/java` | Repository and credential-encryption tests |
| `app-java17/build.gradle` | Java 17 variant and dependencies |
| `app-java21/build.gradle` | Java 21 variant, compiling the shared Java 17 source tree |
| `app-java25/build.gradle` | Java 25 variant, compiling the shared Java 17 source tree |
| `gradle/libs.versions.toml` | Central dependency versions |

Lombok is configured as a compile-time and test annotation processor in every variant. `Project` uses Lombok for its builder, accessors, equality, and string representation.

## Requirements

- JDK 17, 21, and 25 available to Gradle toolchains.
- Docker Desktop or Docker Engine for the PostgreSQL example.
- The PostgreSQL compose stack in `Docker_scripts/postgres-db-admin`.

Start PostgreSQL from the directory containing that compose file:

```powershell
docker compose up -d postgres
docker compose ps
```

The compose database is exposed on host port `5432` as database `appdb`, user `Admin`, password `Pwd`. The application connects to host `localhost`, provisions its own database named `test-jdbc`, then creates its tables there. These are demonstration credentials, not production credentials.

## Build, Test, and Run

Run all tests on all supported toolchains:

```powershell
.\gradlew.bat :app-java17:test :app-java21:test :app-java25:test
```

Run the PostgreSQL application with one Java version:

```powershell
.\gradlew.bat :app-java17:run
.\gradlew.bat :app-java21:run
.\gradlew.bat :app-java25:run
```

Each run prints the PostgreSQL URL and number of persisted projects. The main class is `foundation.jdbc.App` in the shared source tree.

The Java 17, 21, and 25 Gradle projects all use the Java 17 project's shared source and resource directories. In particular, they all read the same `app-java17/src/main/resources/db-config.yml`; do not create three divergent copies of the config.

### YAML Configuration Shape

The config is a YAML document with one top-level `db` mapping. Every value below is a nonblank string, including encrypted fields and nonces:

```yaml
db:
	databaseName: test-jdbc
	adminUrl: jdbc:postgresql://localhost:5432/postgres
	url: jdbc:postgresql://localhost:5432/test-jdbc
	salt: <base64-encoded random salt, at least 16 bytes>
	username: ENC(<base64 AES-GCM ciphertext>)
	usernameNonce: <base64 12-byte nonce for username>
	password: ENC(<base64 AES-GCM ciphertext>)
	passwordNonce: <base64 12-byte nonce for password>
```

The angle-bracket values are placeholders, not runnable credentials. The checked-in `db-config.yml` contains complete demo values. `adminUrl` points to an existing PostgreSQL database where the app can create `databaseName`; `url` points to that application database. `salt` is shared when deriving the key, but each ciphertext must keep its own matching nonce. The `ENC(...)` wrapper is optional to the decryptor, but is used in the example to make ciphertext recognizable.

### How Configuration Is Read

At startup, `App` reads `FOUNDATION_DB_CONFIG_SECRET` (or warns and uses the public demo key), then calls `EncryptedDatabaseConfig.load("/db-config.yml", secret)`. SnakeYAML parses the classpath resource, the loader checks the `db` mapping and required string values, derives an AES-256 key from the secret and salt with PBKDF2-HMAC-SHA256, and authenticates/decrypts the username and password with AES-GCM. Missing values or an incorrect secret stop startup with an error.

The resulting config contains plaintext credentials only in memory. `App` then applies optional `FOUNDATION_DB_ADMIN_URL` and `FOUNDATION_DB_URL` overrides, creates the application database if needed, and opens the HikariCP connection pool using the decrypted credentials. The YAML URLs are defaults; URL overrides do not change which credentials are decrypted.

## Credential Encryption Demo

`CredentialCipher` encrypts UTF-8 values with AES-GCM. The AES-256 key is derived from a passphrase and the YAML salt using PBKDF2-HMAC-SHA256 (310,000 iterations). Every encryption call generates its own 12-byte nonce; AES-GCM authenticates ciphertext so modified values or an incorrect key fail decryption.

`EncryptedDatabaseConfig` reads `/db-config.yml` from the classpath. Set the decryption passphrase outside the source tree:

```powershell
$env:FOUNDATION_DB_CONFIG_SECRET = 'your-demo-passphrase'
.\gradlew.bat :app-java21:run
```

The committed ciphertext was generated using the explicit example fallback key in `App`. With no environment variable, startup prints a warning and uses that fallback so the example runs immediately. **This fallback is public and provides no meaningful secrecy; it is demonstration-only.** If you set a different passphrase, regenerate the encrypted username and password and their nonces using `CredentialCipher.encrypt`, then update `db-config.yml`. Never commit a real passphrase or production database credentials.

For production, prefer a secret manager or deployment-provided secret and remove the fallback. Encryption at rest does not protect a key stored beside ciphertext, and the compose file itself still contains the local PostgreSQL demo password in plaintext.

## Repository Examples

All repositories implement `ProjectRepository`, which exposes create, find-by-id, list, update, and delete methods. `JdbcProjectRepository` contains the shared JDBC mapping, schema setup, and transaction handling.

- `HikariPostgresProjectRepository` is the pooled PostgreSQL implementation used by `App`. `HikariDataSource` implements `javax.sql.DataSource`; closing the repository closes the pool.
- `PostgresProjectRepository` is the unpooled comparison. Its `DriverManager` adapter implements `javax.sql.DataSource` and opens a fresh connection for each request.
- `H2ProjectRepository` runs against H2; by default it uses an in-memory database.
- `SQLiteProjectRepository` runs against SQLite; by default it creates `foundation-jdbc.db` in the working directory.

H2 example:

```java
ProjectOwner owner = new ProjectOwner();
Project project = Project.builder()
		.projectOwner(owner)
		.projectName("Example project")
		.sponsor("Foundation")
		.budget(1500.0)
		.startDate(LocalDate.of(2026, 2, 1))
		.deadline(LocalDate.of(2026, 12, 1))
		.registeredOn(LocalDate.of(2026, 1, 2))
		.build();

try (ProjectRepository repository = new H2ProjectRepository()) {
	long id = repository.create(project);
	Project saved = repository.findById(id).orElseThrow();
}
```

Add imports for `Project`, `ProjectOwner`, `ProjectRepository`, `H2ProjectRepository`, and `java.time.LocalDate` when trying this in a class.

SQLite accepts a custom URL, for example `jdbc:sqlite:path/to/projects.db`. Both test repositories are exercised by `ProjectRepositoryTest`; run the main app against compose PostgreSQL to exercise the pooled backend.

## Persisted Model

The JDBC schema consists of three tables:

- `project_owners` stores the stable UUID identity for an owner.
- `projects` stores each project's owner, details, and current lifecycle status.
- `project_status_history` stores the date each status was reached and cascades on project deletion.

A new `Project` starts as `REGISTERED`. Allowed transitions are `REGISTERED -> PLANNED -> IMPLEMENTATING -> FINISHED`; cancellation is allowed from the first three states. Status dates cannot move backward. `ProjectOwner` returns immutable snapshots of its project list.

Start with `ProjectRepositoryTest` for CRUD/history round-trips and `CredentialCipherTest` for encryption, decryption, and wrong-key behavior.