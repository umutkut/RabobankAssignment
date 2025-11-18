# Rabobank Authorizations (Power of Attorney)

A simplified Power of Attorney (PoA) service where a grantor can give a grantee Read or Write authorization on Payment
or Savings accounts. Users can:

- Create a new PoA (grant Read/Write on an account)
- Retrieve the accounts they have access to

This repository is a Maven multi‑module project:

- domain: Pure domain model and repository interfaces
- data: Spring Data MongoDB implementation
- api: Spring MVC REST layer

## Prerequisites

- Java 21 (ensure your JDK is 21; the build is configured for source/target 21)
- Maven 3.9+
- mongoimport and mongosh to run the sample accounts script

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

## Test Coverage (JaCoCo)

- Run coverage for individual modules
  ```bash
  mvn -pl api -am clean verify
  mvn -pl data -am clean verify
  mvn -pl domain -am clean verify
  ```

- Per-module coverage reports:
    - API: `api/target/site/jacoco/index.html`
    - Data: `data/target/site/jacoco/index.html`
    - Domain: `domain/target/site/jacoco/index.html`

## Run Locally

### 1) Run the API

From the repo root:

```bash
mvn spring-boot:run main-class=nl.rabobank.RaboAssignmentApplication
```

The data module defaults (see `data/src/main/resources/application.properties`):

- `spring.data.mongodb.host=localhost`
- `spring.data.mongodb.port=27027`
- `spring.data.mongodb.database=test`

Once started, the API listens on the default Spring Boot port (8080).
Embedded mongo will start at localhost 27027

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


## Try It Out

- Import the Postman collection: `postman/PowerOfAttorney.postman_collection.json`
- Point requests to your local server base URL

## API Endpoints (current implementation)

- POST /api/v1/account/authorization
    - Create a new PoA. 201 Created with Location header `/api/v1/account/authorization/{id}`.
- GET /api/v1/account/authorization/{id}
    - Get PoA details by id.
- PUT /api/v1/account/authorization/{id}?newAuthorization=READ|WRITE
    - Update authorization type using `newAuthorization` query parameter.
- DELETE /api/v1/account/authorization/{id}?grantorName={name}
    - Delete PoA; only allowed when the provided `grantorName` matches the PoA grantor.
- GET /api/v1/account/accessible-by/{granteeName}
    - List accounts accessible by the given user. Ownership access is always included.
    - Response items include an `authorization` field indicating the effective access for the requester:
        - Owned accounts: `authorization` is `WRITE` (implicit, not stored as PoA)
        - Delegated accounts: `authorization` is the PoA-provided value (`READ` or `WRITE`)
- GET /api/v1/account/authorization/granted-by/{grantorName}
    - List PoAs created by the given grantor.
- GET /api/v1/audits/actor/{actorName}
    - List audit logs performed by the given actor. Paged response with defaults: size=5, sort=createdAt. Supports
      `page`, `size`, `sort`.
- GET /api/v1/audits/account/{accountNumber}
    - List audit logs related to the given account number. Paged response with defaults: size=5, sort=createdAt.
      Supports `page`, `size`, `sort`.

## Repo Highlights

- [ORIGINAL_ASSIGNMENT.md](ORIGINAL_ASSIGNMENT.md)`ORIGINAL_ASSIGNMENT.md`: original assignment brief
- `scripts/`[load-accounts.sh](scripts/load-accounts.sh): helper to load mock accounts into Mongo
- `mock/`[accounts.json](mock/accounts.json): sample accounts data
- `postman/`[PowerOfAttorney.postman_collection.json](postman/PowerOfAttorney.postman_collection.json): requests to
  exercise the API
- [QUESTIONS.md](QUESTIONS.md): the questions raised before starting the assignments and self-given answers to clarify
  the requirements
- [REQUIREMENTS.md](REQUIREMENTS.md): the extracted requirements from questions