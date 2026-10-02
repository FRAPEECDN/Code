# MongoDB + Mongo Express (Local Development)

Start both services from this folder:

```sh
docker compose up -d
```

Mongo Express is available at http://localhost:8081. MongoDB is available at `localhost:27017`.

The MongoDB root user is `Admin`, and the example database is `appdb`. The init script creates a `users` collection with a sample document. Mongo Express uses the credentials configured in `docker-compose.yml`.

Stop the services and preserve their data:

```sh
docker compose down
```

Delete the stored database data and start again:

```sh
docker compose down -v
docker compose up -d
```

Despite this folder's name, the Compose file does not use Docker Secrets: its development credentials are set directly in environment variables. Do not reuse them outside local testing; replace them before sharing or deploying this configuration.
