# Foundation Gradle Template

A reusable Gradle multi-project starter. It builds the same shared Java code with Java 17, 21, and 25, and supports version-specific code in each module.

## Structure

```text
shared/src/main/java/       Common application code
shared/src/test/java/       Tests run by all three modules
java17/src/main/java/       Java 17-only code, when needed
java21/src/main/java/       Java 21-only code, when needed
java25/src/main/java/       Java 25-only code, when needed
```

Lombok is configured for main and test sources. Tests use JUnit 5.

## Run

```powershell
.\gradlew.bat build
.\gradlew.bat :java17:run
.\gradlew.bat :java21:run
.\gradlew.bat :java25:run
```

Each module selects its JDK through a Gradle toolchain. The Foojay resolver can provision a missing JDK when network access is available.