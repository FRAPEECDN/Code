PostgreSQL Docker Setup Using Docker Secrets

Folder Structure:
- docker-compose.yml
- postgres/init/01-init.sql
- secrets/postgres_password.txt

How To Run:

1. Install Docker Desktop for Windows

2. Open terminal in this folder

3. Start PostgreSQL:

   docker compose up -d

4. Connect to PostgreSQL:

   docker exec -it postgres-db psql -U Admin -d appdb

5. Stop container:

   docker compose down

6. Reset database completely:

   docker compose down -v
   docker compose up -d

Security Notes:
- Password is stored using Docker Secrets
- Password is NOT directly inside docker-compose.yml
- PostgreSQL reads the password securely from:
  /run/secrets/postgres_password
