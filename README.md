# 📦 CIAMS_project

--- 

CIAMS is a Spring Boot RESTful application designed for managing an IT inventory, including physical assets, offices, asset types, and software licenses.

It simulates a real-world enterprise backend, focusing on:

* clean architecture
* explicit business logic
* security
* maintainability


The application allows tracking where assets are located, which licenses are installed on them, and monitoring license expiration.

---

## 🖥️ Main Features

### Asset Management
* Create, update, list and delete IT assets
* Assign assets to offices
* Move assets between offices
* Decommission assets

### Office & Asset Type Management
* CRUD operations for Offices
* CRUD operations for Asset Types
* Cached read operations for improved performance

### Software License Management
* CRUD operations for software licenses
* Install / uninstall licenses on assets (many-to-many)
* Retrieve asset details including installed licenses
* Retrieve licenses expiring within the next 30 days

### Security
* Basic Authentication
* Read operations publicly accessible
* Write operations restricted to authenticated users
* Custom authentication entry point and access denied handler

---

## 🏗️ Architecture Overview

The application follows a **layered architecture**, with strict separation of concerns:

```
Controller → Service → Repository → Database

                ↓
                Mapper
                
                ↓
                DTO
                
```

### Layer Responsibilities

- **Controller**
  - Exposes REST endpoints and handles HTTP concerns

- **Service**
  - Contains business logic and transactional boundaries

- **Repository**
  - Handles persistence via Spring Data JPA
  - Provides query methods based on domain concepts

- **Entity**
  - Represents the persistent domain model
  - Defines JPA mappings and relationships
  - Mirrors the logical database schema

- **DTO (Request / Response)**
  - Prevents exposing JPA entities directly
  - Uses human-friendly keys (names, serial numbers) instead of internal IDs

- **Mapper (with MapStruct library)**
  - Converts between Entities and DTOs automatically

- **Exception**
  - Centralized via `@RestControllerAdvice`
  - Maps exceptions to appropriate HTTP status codes
  - Ensures a consistent JSON error response format across the API

- **Security**
  - Implemented with Spring Security
  - Manages security-related error responses (401 Unauthorized, 403 Forbidden)

- **Utils**
  - Input normalization utilities for clean and consistent data (e.g. lowercase keys, trimmed values)

---

## 🧱 Domain Model 

### Office
Represents a physical company location.

- `id`
- `name` (unique, domain key)

### AssetType
Defines a standardized hardware category.

- `id`
- `assetTypeName` (unique, domain key)
- `assetTypeDescription` (optional)

### Asset
Represents a physical device owned by the company.

- `id`
- `serialNumber` (unique, domain key)
- `purchaseDate`
- `office` (Many-to-One)
- `assetType` (Many-to-One)
- `softwareLicenses` (Many-to-Many)

### SoftwareLicense
Represents a purchased software license.

- `id`
- `softwareName` (unique, domain key)
- `expirationDate`
- `maxInstallations` (nullable, null = unlimited)
- `installedAssets` (Many-to-Many)

---

## 🔌 REST API Overview

A Postman collection is provided to test:

* CRUD operations
* License installation/uninstallation
* Authentication-protected endpoints
* Error scenarios


### **MAIN ENDPOINTS**

### Office Management

- `GET /offices/all`
- `GET /offices/{officeName}`
- `POST /offices/insert`
- `PUT /offices/{officeName}`
- `DELETE /offices/{officeName}`

### Asset Type Management

- `GET /asset-types/all`
- `GET /asset-types/{assetTypeName}`
- `PUT /asset-types/{assetTypeName}`
- `POST /asset-types/insert`
- `DELETE /asset-types/{assetTypeName}`

### Asset Management

#### Base Views

- `GET /assets/all`
- `GET /assets/{serialNumber}`
- `GET /assets/office/{officeName}`
- `GET /assets/type/{assetTypeName}`

#### Detailed View

- `GET /assets/{serialNumber}/details`

#### Write Operations

- `POST /assets/insert`
- `PUT /assets/update/{serialNumber}`

##### Move asset to another office

- `PUT /assets/{serialNumber}/move?officeName={officeName}`
- `DELETE /assets/{serialNumber}`


### Software License Management

#### CRUD Operations

- `GET /software-licenses/all`
- `GET /software-licenses/{softwareName}`
- `POST /software-licenses/insert`
- `PUT /software-licenses/update/{softwareName}`
- `DELETE /software-licenses/{softwareName}`

#### Compliance Operations

Install license on an asset
- `POST /software-licenses/{softwareName}/install/{serialNumber}`

Uninstall license from an asset
- `DELETE /software-licenses/{softwareName}/uninstall/{serialNumber}`

Audit licenses installed on an asset
- `GET /software-licenses/asset/{serialNumber}`

Retrieve licenses expiring soon
- `GET /software-licenses/expiring-soon`


---

## 🛠️ Tech Stack

### Application
- Java 17
- Spring Boot 3.5
- Spring Data JPA / Hibernate**
- Spring Security
- MapStruct
- Lombok
- Jakarta Validation
- PostgreSQL
- Gradle
- SLF4J Logging

### Containerization & Runtime
- Docker
- Docker Compose
- Podman
- Podman Compose

---


## 🚀 Getting Started

1. Clone the repository
2. Configure environment variables for database and security credentials 
3. Run the application
4. Test endpoints using Postman or similar tools

The application can be run either locally or using containerized environments (Docker / Podman).

---

## ⚙️ Setup & Run

*Prerequisites* 

Make sure the following tools are installed on your system:

- **Java 17**

- A relational database:

  - **PostgreSQL (recommended) or MySQL**
  - **An API testing tool (Postman, Insomnia, etc.)**

*Database Configuration*

Create a database for the application (example with PostgreSQL):

```sql
CREATE DATABASE inventory_db;
```

Database credentials and connection details are externalized using environment variables and loaded at runtime via a `.env` file.

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=inventory_db
DB_USER=your_db_username
DB_PASSWORD=your_db_password
```

In `application.yaml` (or `application.properties`) use placeholders:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

*Schema & Initial Data*

Database schema is initialized via `schema.sql`.
Sample data is loaded via `data.sql`.

These scripts allow the application to start with a consistent and usable dataset for testing and demonstration purposes.

*Entity relationships*

The database schema reflects the core domain model:

1. Asset → Office (many-to-one)
2. Asset → AssetType (many-to-one)
3. Asset ↔ SoftwareLicense (many-to-many)

All relationships are enforced at the JPA level and validated at the service layer.

*Build the Project*

From the project root directory, run:

```bash
./gradlew build
```

This will:

- compile the project
- run validations
- generate MapStruct mappers
- package the application

*Run the Application using Gradle*

```bash
./gradlew bootRun
```

*Application Startup*

Once started, the application will be available at: http://localhost:8080

Example endpoint: http://localhost:8080/offices/all

---

## 🐳 Docker & Docker Compose

The application is also designed to run in containerized environments.
Both Docker and Podman are supported.
Docker Compose and Podman Compose are used for local orchestration.

### Run with Docker

```bash
docker-compose up --build
```

### Run with Podman
```bash
podman-compose up --build
```



## 🔮 Future Improvements

While CIAMS already provides a complete and consistent backend for IT asset and software license management, several enhancements could further improve scalability, usability, and maintainability.

1. Swagger / OpenAPI documentation

2. Frontend & Visualization Layer

Although CIAMS is currently backend-focused, a future extension could include a web-based frontend.
This would improve usability for non-technical users.

## 📄 License

This project is intended for educational and professional demonstration purposes.


---

## 👤 Author - Romina Trazzi


