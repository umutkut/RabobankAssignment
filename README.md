# Rabobank Authorizations (Power of Attorney)

A simplified Power of Attorney (PoA) service where a grantor can give a grantee Read or Write authorization on Payment
or Savings accounts. Users can:

- Create a new PoA (grant Read/Write on an account)
- Retrieve the accounts they have access to

This repository is a Maven multi‑module project:

- domain: Pure domain model and repository interfaces (no Spring)
- data: Spring Data MongoDB implementation
- api: Spring MVC REST layer

## Prerequisites

- Java 21 (ensure your JDK is 21; the build is configured for source/target 21)
- Maven 3.9+
- Docker (for local MongoDB via docker compose)

## Build and Test

- Full build (skip tests):
  ```bash
  mvn -q -DskipTests clean install
  ```
- Full build with tests:
  ```bash
  mvn clean verify
  ```
- Per-module (faster iteration):
  ```bash
  mvn -pl api test
  mvn -pl data test
  mvn -pl domain test
  ```

## Run Locally

### 1) Start MongoDB (Docker Compose)

From the repo root:

```bash
docker compose up -d mongo
```

This uses `docker-compose.yml` to start a local MongoDB 7 instance named `rabobank-mongo` on port `27017`, with default
database `test`.

### 2) Load sample accounts (optional but recommended)

From the repo root:

```bash
bash scripts/load-accounts.sh
```

What it does:

- Ensures the `rabobank-mongo` container is running (starts it if needed)
- Copies `mock/accounts.json` into the container
- Upserts into `test.accounts` via `mongoimport`
- Prints the resulting document count

### 3) Run the API

From the repo root:

```bash
mvn spring-boot:run main-class=nl.rabobank.RaboAssignmentApplication
```

The data module defaults (see `data/src/main/resources/application.properties`):

- `spring.data.mongodb.host=localhost`
- `spring.data.mongodb.port=27017`
- `spring.data.mongodb.database=test`

Once started, the API listens on the default Spring Boot port (8081).

## Try It Out

- Import the Postman collection: `postman/PowerOfAttorney.postman_collection.json`
- Point requests to your local server base URL

## Repo Highlights

- [ORIGINAL_ASSIGNMENT.md](ORIGINAL_ASSIGNMENT.md)`ORIGINAL_ASSIGNMENT.md`: original assignment brief
- [docker-compose.yml](docker-compose.yml)`docker-compose.yml`: local MongoDB service definition
- `scripts/`[load-accounts.sh](scripts/load-accounts.sh): helper to load mock accounts into Mongo
- `mock/`[accounts.json](mock/accounts.json): sample accounts data
- `postman/`[PowerOfAttorney.postman_collection.json](postman/PowerOfAttorney.postman_collection.json): requests to
  exercise the API
- [QUESTIONS.md](QUESTIONS.md): the questions raised before starting the assignments and self-given answers to clarify
  the requirements
- [REQUIREMENTS.md](REQUIREMENTS.md)