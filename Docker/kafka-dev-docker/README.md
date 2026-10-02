# Kafka Dev Test Environment

A small but realistic Apache Kafka environment for application development.

## What it provides

- Apache Kafka 4.3.1
- KRaft mode; no ZooKeeper
- One broker/controller (intentionally non-production)
- 2 partitions on `dev-test`
- SASL/SCRAM-SHA-256 client authentication
- Kafka ACL authorization
- Separate `admin` and `dev` users
- Consumer groups such as `dev-my-app`
- Persistent Docker volume
- Automatic topic/user/ACL initialization
- Host connection: `localhost:9094`
- Container-to-container connection: `kafka:9092`

Kafka's official Docker documentation currently lists 4.3.1 as a supported release. SCRAM-SHA-256 is used here because it exercises real authentication while keeping the local setup simple. For production, use TLS with SCRAM (SASL_SSL), not SASL_PLAINTEXT.

## Start

```bash
docker compose up -d
docker compose logs -f kafka-init
```

Check:

```bash
docker compose ps
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh   --bootstrap-server kafka:9092   --command-config /config/admin.properties   --list
```

## Client configuration

Use `config/client.properties` from an application running on the host, changing the bootstrap server to:

```text
localhost:9094
```

For Java/Spring:

```properties
spring.kafka.bootstrap-servers=localhost:9094
spring.kafka.properties.security.protocol=SASL_PLAINTEXT
spring.kafka.properties.sasl.mechanism=SCRAM-SHA-256
spring.kafka.properties.sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="dev" password="dev-secret";
```

## Test producer

```bash
docker compose exec kafka /opt/kafka/bin/kafka-console-producer.sh   --bootstrap-server kafka:9092   --producer.config /config/client.properties   --topic dev-test
```

Type messages and press Enter.

## Test consumer group

Open two terminals and run:

```bash
docker compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh   --bootstrap-server kafka:9092   --consumer.config /config/client.properties   --topic dev-test   --group dev-my-app
```

Run the same command in the second terminal.

Because `dev-test` has 2 partitions, Kafka can assign the two partitions to the two consumer instances. This is useful for testing concurrency/group behavior.

## Inspect the group

```bash
docker compose exec kafka /opt/kafka/bin/kafka-consumer-groups.sh   --bootstrap-server kafka:9092   --command-config /config/admin.properties   --describe   --group dev-my-app
```

## Inspect the topic

```bash
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh   --bootstrap-server kafka:9092   --command-config /config/admin.properties   --describe   --topic dev-test
```

## Reset everything

```bash
docker compose down -v
```

Then start again with:

```bash
docker compose up -d
```

## Important development note

This is deliberately a single-node environment. It tests:

- real Kafka producer connections
- real Kafka consumer connections
- SASL authentication
- ACL authorization
- topic creation
- partitions
- consumer groups
- partition assignment/rebalancing
- offsets
- retries/timeouts
- application concurrency

It does **not** test broker failover, replication, ISR behavior, rolling upgrades, or production TLS/certificate deployment.

## Suggested application test matrix

For your Kafka libraries, use:

- topic: `dev-test`
- partitions: `2`
- producer user: `dev`
- consumer group: `dev-<application>`
- consumer concurrency: `2`
- start with one consumer instance
- start a second instance
- stop one instance
- restart it
- observe partition reassignment and committed offsets

Then add a second topic with 4 or 8 partitions when testing higher concurrency.
