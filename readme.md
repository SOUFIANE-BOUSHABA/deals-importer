## FX Deals Importer

Spring Boot service to ingest FX deals (JSON or CSV) and persist them into a real DB (PostgreSQL) with idempotency (skip duplicates), per-row no-rollback, validation, logging, Docker Compose, tests, and documentation.

Repo: https://github.com/SOUFIANE-BOUSHABA/deals-importer

Features

# Endpoints

POST /api/deals — JSON array upload

POST /api/deals/upload — multipart CSV upload (file key)

 
# Tech Stack

Java 21, Spring Boot 3.5 (Web, Validation, Data JPA, Actuator)

PostgreSQL 17

Flyway (DB migrations)

Apache Commons CSV (CSV parsing)

JUnit 5, Testcontainers (PostgreSQL)

Maven 3.9.x

Docker / Docker Compose

Project Structure
deals-importer/
├─ docker-compose.yml
├─ Dockerfile
├─ Makefile
├─ README.md
├─ sample/
├─ src/
│  ├─ main/
│  │  ├─ java/com/bbg/fx/fx_deals_importer/
│  │  │  ├─ FxDealsImporterApplication.java
│  │  │  ├─ api/
│  │  │  │  ├─ controller/DealController.java
│  │  │  │  └─ dto/{DealRequest.java, ImportResult.java}
│  │  │  ├─ common/validation/Iso4217.java
│  │  │  ├─ model/Deal.java
│  │  │  ├─ repository/DealRepository.java
│  │  │  └─ service/DealImportService.java
│  │  └─ resources/
│  │     ├─ application.yml
│  │     └─ db/migration/V1__init.sql
│  └─ test/
└─ pom.xml

Prerequisites

Docker + Docker Compose


# Quick Start (Docker)
git clone https://github.com/SOUFIANE-BOUSHABA/deals-importer.git
cd deals-importer

# Build jar then start DB + app (one command via Makefile)
make up

# or, if you don’t use Make:
mvn -DskipTests package
docker compose up -d --build


Wait until the app is healthy (docker compose logs -f app).

Upload sample CSV
# from project root
curl -v -F "file=@sample/01_valid_deals.csv;type=text/csv" \
  http://localhost:8080/api/deals/upload