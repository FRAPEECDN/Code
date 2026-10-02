PostgreSQL + pgAdmin Docker Setup

Included:
- PostgreSQL 16
- pgAdmin 4
- Docker Secrets support

Folder Structure:
- docker-compose.yml
- postgres/init/01-init.sql
- secrets/postgres_password.txt
- secrets/pgadmin_password.txt

How To Run:

1. Install Docker Desktop for Windows

2. Open terminal in this folder

3. Start everything:

   docker compose up -d

4. Open pgAdmin:

   http://localhost:8080

5. Login to pgAdmin:

   Email:
   admin@example.com

   Password:
   (contents of pgadmin_password.txt)

6. Add PostgreSQL Server inside pgAdmin

   Name:
   Local PostgreSQL

   Host:
   postgres

   Username:
   Admin

   Password:
   (contents of postgres_password.txt)

   Database:
   appdb

Useful Commands:

View running containers:
   docker ps

Stop everything:
   docker compose down

Reset database completely:
   docker compose down -v
   docker compose up -d

Security Notes:
- Passwords are stored using Docker Secrets
- Secrets are mounted securely into containers
- Passwords are not directly embedded in docker-compose.yml
