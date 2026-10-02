# Docker Development Environments

This folder contains Docker Compose environments for local development and testing. They are examples, not production deployments.

## Projects

| Folder | Environment | Host ports |
| --- | --- | --- |
| [`kafka-dev-docker`](kafka-dev-docker/) | Single-node Kafka in KRaft mode, with SCRAM authentication and ACLs. See the [project README](kafka-dev-docker/README.md). | `9094` |
| [`kafka-observability-dev/kafka-observability-dev`](kafka-observability-dev/kafka-observability-dev/) | Grafana, Kafka UI, Prometheus, Loki, Tempo, and Grafana Alloy. See the [project README](kafka-observability-dev/kafka-observability-dev/README.md). | `3000`, `8080`, `9090`, `3100`, `3200`, `4317`, `4318`, `12345` |
| [`mongodb-docker-secrets-package`](mongodb-docker-secrets-package/) | MongoDB with Mongo Express. See the [project README](mongodb-docker-secrets-package/README.txt). | `27017`, `8081` |
| [`postgres-db`](postgres-db/) | PostgreSQL with a file-backed Compose secret. See the [project README](postgres-db/README.txt). | `5432` |
| [`postgres-db-admin`](postgres-db-admin/) | PostgreSQL with pgAdmin and file-backed Compose secrets. See the [project README](postgres-db-admin/README.txt). | `5432`, `8080` |

## Run an Environment

Install Docker Desktop or Docker Engine with the Docker Compose plugin. Open a terminal in the selected project folder, then start it:

```powershell
docker compose up -d
docker compose ps
```

For example, from the repository root:

```powershell
Set-Location Docker/kafka-dev-docker
docker compose up -d
docker compose logs -f kafka-init
```

Stop the selected environment while preserving its data:

```powershell
docker compose down
```

## Dependencies and Port Conflicts

- Start `kafka-dev-docker` before `kafka-observability-dev`. The observability Compose project connects to the external `kafka-dev_default` network created by the Kafka project.
- Several environments use the same host ports: Kafka UI and pgAdmin both use `8080`; Mongo Express and the example Java metrics endpoint use `8081`; both PostgreSQL projects use `5432`. Run conflicting environments separately or change their port mappings.
- `docker compose down -v` also deletes the environment's persistent data volumes. Use it only when you intend to reset stored data.

## Credentials

These configurations use development credentials and are not production-secure. The MongoDB Compose file currently sets credentials directly in environment variables; despite the folder name, it does not configure Docker Secrets. The PostgreSQL projects use local files under their `secrets/` folders as Compose secrets. Replace development credentials and protect real secret files before using or publishing an environment.