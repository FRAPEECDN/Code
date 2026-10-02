# Kafka Development Observability Stack

This package adds a local observability environment around the Kafka development
cluster from the companion `kafka-dev-docker` package.

## Components

- Grafana: http://localhost:3000
- Kafka UI: http://localhost:8080
- Prometheus: http://localhost:9090
- Loki: http://localhost:3100
- Tempo: http://localhost:3200
- Grafana Alloy: http://localhost:12345

Grafana login:

    admin / admin

## Startup

Start your Kafka package first:

    docker compose up -d

Then from this directory:

    docker compose up -d

The observability Compose file expects the Kafka Compose network to be named
`kafka-dev_default`. If your Kafka project generated a different network name,
change the external network name in docker-compose.yml.

## Java application

The companion Gradle example runs on the host and exposes:

    http://localhost:8081/actuator/prometheus

Prometheus is already configured to scrape it.

For OpenTelemetry, configure the Java application with:

    OTEL_SERVICE_NAME=java-kafka-example
    OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318

## Logs

All Docker container stdout/stderr is collected by Grafana Alloy and sent to Loki.

In Grafana Explore, choose Loki and query:

    {container=~".+"}

Example:

    {container="kafka-dev-grafana"}

## Important

This is a development observability stack. It intentionally uses local storage,
single-instance services, simple credentials, and no HA configuration.
