# Foundation MongoDB

A teaching example of a Java application that persists a project lifecycle model in MongoDB. The same shared implementation is compiled and tested with Java 17, 21, and 25 using Gradle.

The project demonstrates explicit BSON mapping, repository boundaries, CRUD, filters, pagination, optimistic updates, bulk writes, aggregation, text search, and transaction handling. Tests use an in-memory MongoDB-compatible server by default; they do not require a local MongoDB installation.

## Learning goals

- Separate lifecycle rules in the model from database access in the repository.
- Give persisted projects and owners stable UUID identities.
- Map model values to BSON explicitly so storage representation is visible.
- Use MongoDB filters, updates, indexes, bulk writes, and aggregation pipelines.
- Test database behavior through the MongoDB wire protocol without a real database process.
- Recognize which operations need a real MongoDB deployment or a replica set.

## Project layout

```text
shared/src/main/java/foundation/mongodb/App.java
                            Runnable demonstration of repository operations
shared/src/main/java/foundation/mongodb/model/
                            Project, ProjectOwner, and ProjectStatus
shared/src/main/java/foundation/mongodb/repository/
                            ProjectRepository and BSON mapping
shared/src/test/java/      In-memory repository integration tests
java17/                    Java 17 toolchain subproject
java21/                    Java 21 toolchain subproject
java25/                    Java 25 toolchain subproject
gradle/libs.versions.toml  Central dependency versions
docker-compose.yml         Optional MongoDB 8 single-node replica set
```

Shared source directories are added to all three Gradle subprojects, so every version builds the same implementation and test suite. Version-specific source can be added beneath each `javaXX/src/main/java` directory when needed.

## Requirements

- A JDK 17, 21, or 25 for the corresponding module. The Gradle Foojay toolchain resolver can provision a missing JDK when network access is available.
- Docker Compose only when running the optional MongoDB service or the real-server integration test.

## Build and test

Run from this directory in PowerShell:

```powershell
.\gradlew.bat build
```

This builds and tests all three toolchain modules. Run one module at a time with:

```powershell
.\gradlew.bat :java17:test
.\gradlew.bat :java21:test
.\gradlew.bat :java25:test
```

Generate Javadocs for a module with:

```powershell
.\gradlew.bat :java17:javadoc
```

## Connect to MongoDB

The application reads these environment variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `MONGODB_URI` | `mongodb://localhost:27017` | MongoDB Java driver connection string |
| `MONGODB_DATABASE` | `foundation` | Database used by the demo |

When using the included Compose configuration, start its MongoDB 8 single-node replica set:

```powershell
docker compose up -d
.\gradlew.bat :java17:run
```

The Compose service does not enable authentication. Its port is `27017`; do not start it if another container or local MongoDB already owns that host port. The replica set is required for multi-document transactions.

For an existing authenticated container that publishes port `27017`, supply its credentials through `MONGODB_URI`. For example, set a URI in this form in the current PowerShell session:

```powershell
$env:MONGODB_URI = "mongodb://<username>:<password>@localhost:27017/?authSource=admin"
$env:MONGODB_DATABASE = "foundation"
.\gradlew.bat :java17:run
```

Replace the placeholders with the credentials configured for that MongoDB instance. Percent-encode reserved characters in URI usernames or passwords. Never commit a connection string containing credentials. The application does not discover credentials from Docker or `mongo-express` automatically.

## What the application does

`foundation.mongodb.App` connects, ensures a text index, and then runs a compact end-to-end demonstration:

1. Builds deterministic demo projects owned by a dedicated demo owner.
2. Bulk-upserts them and runs a transaction when the server advertises replica-set membership.
3. Performs an optimistic status transition and looks up projects by ID, owner, and status.
4. Runs list, regex search, full-text search, paging, budget/date filters, counts, and existence checks.
5. Aggregates total budgets by status and lists distinct sponsors.
6. Demonstrates create and delete operations, then removes only projects belonging to the demo owner.

The deterministic IDs make repeat runs update the same demo records. The final cleanup is deliberately scoped to the app's fixed demo owner; it does not delete unrelated project documents. The transaction step is skipped with a message when connected to a standalone MongoDB server.

## Repository API

`ProjectRepository` owns persistence and BSON conversion. It does not own or close the `MongoClient` passed to its constructor.

| Operation | Repository method | Behavior |
| --- | --- | --- |
| Insert | `create(project)` | Inserts a new document; duplicate project IDs fail |
| Upsert | `save(project)` | Replaces by project UUID or inserts when absent |
| Bulk upsert | `bulkSave(projects)` | Sends replacements as one bulk write |
| Transactional upsert | `saveAllTransactionally(projects)` | Runs the bulk write atomically; requires a client and replica set or sharded cluster |
| Atomic lifecycle update | `updateStatus(id, status, date)` | Validates the transition and compares the stored prior status to avoid overwriting a concurrent transition |
| ID lookup | `findById(id)` | Returns an `Optional<Project>` |
| Owner/status lookup | `findByOwnerId(id)`, `findByStatus(status)` | Returns matching projects sorted by name |
| Literal regex search | `search(term)` | Case-insensitive substring search over project name and sponsor; regex metacharacters are escaped |
| Full-text search | `ensureTextSearchIndex()`, `textSearch(term)` | Uses MongoDB's compound text index over project name and sponsor |
| Range filters | `findByBudgetRange(min, max)`, `findByDeadlineRange(start, end)` | Inclusive bounds |
| Paging | `findPage(page, pageSize)` | Zero-based pages, sorted by project name |
| Counts | `count()`, `countByStatus(status)`, `exists(id)` | Counts documents or checks an ID without decoding full projects |
| Aggregation | `totalBudgetByStatus()` | Groups projects and sums their budgets by lifecycle state |
| Distinct values | `findDistinctSponsors()` | Returns unique sponsor names, sorted without regard to case |
| Delete | `delete(id)`, `deleteByOwnerId(id)`, `deleteByOwnerIdAndStatus(id, status)` | Deletes by ID or scoped owner filters; delete methods return whether/count of removed documents |

MongoDB creates owner and status indexes when a repository is constructed. The text index is created explicitly by `ensureTextSearchIndex()` because not all MongoDB-compatible test servers implement text indexes.

## Stored document

The repository maps `Project` objects to a `projects` collection document similar to:

```json
{
  "_id": "project UUID string",
  "ownerId": "owner UUID string",
  "projectName": "Atlas observability",
  "sponsor": "Northwind",
  "budget": 125000.0,
  "startDate": "2026-01-01",
  "deadline": "2026-12-31",
  "status": "PLANNED",
  "statusHistory": {
    "REGISTERED": "2026-01-01",
    "PLANNED": "2026-02-01"
  }
}
```

UUIDs are strings, dates are ISO-8601 strings, and status history is an embedded document keyed by enum name. ISO date strings sort chronologically for the fixed-width `yyyy-MM-dd` format used by `LocalDate`.

`Project` owns the lifecycle rules: allowed transitions follow `ProjectStatus`, transition dates cannot move backward, and callers receive immutable history snapshots. Constructing a `Project` also registers it with its `ProjectOwner` in memory; persistence of the project document does not separately persist an owner document.

## Test strategy

The standard suite starts `mongo-java-server` with its memory backend on a random local port. It speaks the MongoDB wire protocol, so tests exercise the synchronous Java driver and repository without Docker. These tests cover CRUD, model reconstruction, filters, paging, count/existence, bulk upsert, aggregation, distinct values, optimistic updates, and scoped deletes.

The in-memory implementation does not support transactions or full-text search. One opt-in integration test covers both using a real replica set. Start Compose, then run:

```powershell
$env:MONGODB_INTEGRATION_URI = "mongodb://localhost:27017/?replicaSet=rs0"
.\gradlew.bat build
```

If the replica set requires authentication, include credentials in `MONGODB_INTEGRATION_URI` using the same URI rules described above. The integration test creates a uniquely named temporary database and drops it after successful assertions.

## Design notes and limitations

- `save` is a full-document replacement, not a partial update. Use `updateStatus` for the atomic lifecycle operation.
- The transaction helper requires the repository to be constructed with both a `MongoClient` and `MongoDatabase`. The database-only constructor intentionally does not enable transactions.
- Transactions require a MongoDB replica set or sharded cluster. The sample app checks the `hello` response and skips the transaction demonstration on standalone deployments.
- Full-text search requires the text index; regex search does not.
- `mongo-java-server` is a test double, not the MongoDB server implementation. Its command and feature support is narrower than MongoDB's.
- The sample app prints MongoDB driver warnings if no SLF4J implementation is configured; this does not prevent database operations.
