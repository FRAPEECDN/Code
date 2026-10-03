# Local Development Observability Stack

This stack collects telemetry from Kafka and other local development projects.
Prometheus, Loki, Tempo, Alloy, and Grafana work without Kafka. Kafka UI is
optional and is enabled with the `kafka` Compose profile.

## Components

- Grafana: http://localhost:3000
- Kafka UI (optional): http://localhost:8080
- Prometheus: http://localhost:9090
- Loki: http://localhost:3100
- Tempo: http://localhost:3200
- Grafana Alloy: http://localhost:12345

Grafana login:

    admin / admin

## Startup

From the workspace root, start the shared observability services:

    docker compose -f kafka-observability-dev/kafka-observability-dev/docker-compose.yml up -d

To include Kafka UI, start Kafka first and enable its profile:

    docker compose -f kafka-dev-docker/docker-compose.yml up -d
    docker compose --profile kafka -f kafka-observability-dev/kafka-observability-dev/docker-compose.yml up -d

Kafka UI joins the stable `kafka-dev_default` network and reaches the broker at
`kafka:9092`. Without the profile, the rest of observability has no Kafka
network dependency.

## Add an application

### Logs

All Docker container stdout/stderr is collected automatically by Alloy, even
when the container belongs to another Compose project or does not use Kafka.
No shared Docker network is needed for logs. In Grafana Explore, select Loki
and filter by container or Compose service, for example:

    {compose_service="inventory-api"}

This Docker log collection does not include applications run directly on the
host. Run those apps in containers or add a file source to Alloy when you need
their host-side log files collected.

### Metrics

Prometheus scrapes the Spring Boot host example at
`host.docker.internal:8081/actuator/prometheus`. Include
`micrometer-registry-prometheus` and expose the endpoint, for example:

    management.endpoints.web.exposure.include=health,prometheus

Add a scrape job in `prometheus/prometheus.yml` for each additional app. For an
app on the host, use its host port and metrics path, for example:

    - job_name: inventory-api
      metrics_path: /actuator/prometheus
      static_configs:
        - targets: ["host.docker.internal:8082"]

For a containerized app, attach it to the external observability network and
target its Compose service name and container port instead. Add the service and
network to that app's Compose file; the network name is
`kafka-observability-dev_observability`:

```yaml
services:
    inventory-api:
        networks: [observability]

networks:
    observability:
        external: true
        name: kafka-observability-dev_observability
```

Then use a target such as `inventory-api:8080`. Reload or restart Prometheus
after changing its configuration.

### Traces

Any OTLP-compatible app can send traces to Tempo. Host processes use
`http://localhost:4318` for OTLP/HTTP or `localhost:4317` for OTLP/gRPC.
Containers on the observability network can use `http://tempo:4318` or
`tempo:4317`.

For a Java app using the OpenTelemetry Java agent, set values like:

    OTEL_SERVICE_NAME=inventory-api
    OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318

Kafka clients are optional; when needed, use the `dev` SCRAM-SHA-256
credentials and bootstrap server `localhost:9094` from the companion
`kafka-dev-docker` README.

### Dashboards

Add dashboard JSON files under `grafana/dashboards/`; the file provider loads
new dashboards into the `Local Development` folder. The existing Java Kafka
dashboard can remain in its original folder in an already-used Grafana volume.
Dashboards can use any provisioned data source: Prometheus, Loki, or Tempo.

## Important

This is a development observability stack. It intentionally uses local storage,
single-instance services, simple credentials, and no HA configuration.
