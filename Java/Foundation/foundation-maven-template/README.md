# Foundation Maven Template

A reusable Maven reactor for projects that target Java 17, 21, and 25. Each module compiles the common source and test trees alongside its own standard Maven source tree.

## Structure

```text
pom.xml                         Parent reactor and shared Maven configuration
shared/src/main/java/           Common application code
shared/src/test/java/           Tests compiled and run in each module
java17/src/main/java/            Java 17-specific code, when needed
java21/src/main/java/            Java 21-specific code, when needed
java25/src/main/java/            Java 25-specific code, when needed
```

The parent configures Lombok, JUnit 5, compiler `--release`, Surefire, Maven Enforcer, JaCoCo, and shared source directories. Java 25 preview features are intentionally not enabled by default.

## Build and Test

Run the full reactor with JDK 25 or newer:

```powershell
mvn clean test
```

Build and test one module:

```powershell
mvn -pl java17 test
mvn -pl java21 test
mvn -pl java25 test
```

Run an application:

```powershell
mvn -pl java17 compile exec:java
mvn -pl java21 compile exec:java
mvn -pl java25 compile exec:java
```

Each child sets its own Java release. The Maven Enforcer rule requires the running JDK to be at least that release, so building the full reactor requires JDK 25 or newer. To build an older module independently, use a JDK at least as new as that module's target.