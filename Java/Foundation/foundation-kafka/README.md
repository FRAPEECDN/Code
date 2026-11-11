# Foundation Kafka

A direct Apache Kafka client demo based on the [Foundation Gradle Template](../foundation-gradle-template/README.md). It has separate producer and consumer applications for Java 17, 21, and 25. The apps use `org.apache.kafka:kafka-clients` directly; this project does not use Spring or Spring Boot.

The local broker setup is in [kafka-dev-docker](../../../Docker/kafka-dev-docker/README.md). It provides Kafka 4.3.1 on `localhost:9094`, the `dev-test` topic, and a `dev` SASL/SCRAM user with producer and consumer ACLs. All three message formats use that same topic. This project uses the broker's existing topic and credentials; it does not create or manage Docker resources.

## Message formats

The producer lets you choose a format for each message and returns to the menu after sending:

1. **String**: UTF-8 text entered at the prompt.
2. **JSON**: a `DemoMessage` POJO encoded as JSON with Jackson.
3. **Apache Avro**: the same POJO mapped to an Avro record and encoded as Avro binary using the bundled [`demo-message.avsc`](shared/src/main/resources/avro/demo-message.avsc) schema. No Schema Registry is required.

Each Kafka record carries a `foundation-kafka-message-type` header (`STRING`, `JSON`, or `AVRO`). The consumer reads the shared topic and dispatches each record to the matching decoder. Records written by the earlier string-only demo have no type header and are treated as UTF-8 strings for compatibility.

## Modules

| Module | Java release | Application |
| --- | ---: | --- |
| `producer-java17` | 17 | Sends one message to the configured topic. |
| `consumer-java17` | 17 | Reads and commits messages from the configured topic. |
| `producer-java21` | 21 | Same producer, compiled and run with Java 21. |
| `consumer-java21` | 21 | Same consumer, compiled and run with Java 21. |
| `producer-java25` | 25 | Same producer, compiled and run with Java 25. |
| `consumer-java25` | 25 | Same consumer, compiled and run with Java 25. |

The implementation is shared across the Java releases. Gradle toolchains select the matching JDK for each module; the Foojay resolver can provision a missing toolchain when network access is available.

## Configure the client

Start the broker from `Docker/kafka-dev-docker` if it is not already running:

```powershell
docker compose -f ..\..\..\Docker\kafka-dev-docker\docker-compose.yml up -d
docker compose -f ..\..\..\Docker\kafka-dev-docker\docker-compose.yml logs -f kafka-init
```

In each PowerShell window that runs an application, set the client credentials and optional connection settings:

```powershell
$env:KAFKA_USERNAME = 'dev'
$env:KAFKA_PASSWORD = 'dev-secret'
$env:KAFKA_BOOTSTRAP_SERVERS = 'localhost:9094'
$env:KAFKA_TOPIC = 'dev-test'
$env:KAFKA_GROUP_ID = 'dev-foundation-kafka'
```

`KAFKA_USERNAME` and `KAFKA_PASSWORD` are required. The other values default to `localhost:9094`, `dev-test`, and `dev-foundation-kafka`. These credentials are for the local Docker demo only; do not use them in production.

## Run

Open one PowerShell window for a consumer and another for the producer. The consumer automatically displays all three formats. Start it from this project directory:

```powershell
.\gradlew.bat :consumer-java17:run
```

In the other window, start the producer:

```powershell
.\gradlew.bat :producer-java17:run
```

Choose `1`, `2`, or `3` and enter the requested value(s); the producer returns to the menu after each send. Choose `q` to exit. To use Java 21 or Java 25, replace `java17` in either command with `java21` or `java25`. The consumer automatically displays all three formats and remains active until you enter `q` or press Ctrl+C. It starts at the earliest available offset for a new group and commits offsets after processing each poll batch. Reusing the group resumes from its committed offset.

## Build and test

Run all six module builds and tests:

```powershell
.\gradlew.bat build
```

Build and test one producer/consumer pair:

```powershell
.\gradlew.bat :producer-java17:test :consumer-java17:test
```

The tests cover client configuration, message-type routing, and JSON/Avro POJO round trips; they do not require a running Kafka broker. Running the applications requires the Kafka Docker project to be available.
