# Code Examples and Reference

This repository collects development examples and reusable project templates. It is intended as a practical reference for projects I work with.

## Java

`Java/Foundation` contains starter templates for foundational Java projects:

- `foundation-gradle-template` is a Gradle multi-project template targeting Java 17, 21, and 25, with shared code and optional version-specific modules.
- `foundation-maven-template` is the equivalent Maven reactor template, also targeting Java 17, 21, and 25.
- `Spring-boot` is reserved for Spring Boot examples; it is currently empty.

Each Foundation template has its own README with structure and build instructions.

## Docker

`Docker` contains local development environments managed with Docker Compose:

- `kafka-dev-docker`: single-node Kafka for application development and testing.
- `kafka-observability-dev`: Grafana, Prometheus, Loki, Tempo, and Alloy for observing the Kafka environment and Java applications.
- `mongodb-docker-secrets-package`: MongoDB with Mongo Express.
- `postgres-db`: PostgreSQL with Docker Secrets.
- `postgres-db-admin`: PostgreSQL and pgAdmin with Docker Secrets.

The Compose files, initialization scripts, service configuration, and READMEs are part of the examples and should be committed. Local build output, coverage data, IDE metadata, and operating-system files are excluded by `.gitignore`.

## Source and AI

Some examples may be AI-assisted. Code is included as a learning and reference aid, with the goal of understanding how it works.
