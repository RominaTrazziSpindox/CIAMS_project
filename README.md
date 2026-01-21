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
- Spring Data JPA / Hibernate
- Spring Security
- MapStruct
- Lombok
- Jakarta Validation
- PostgreSQL
- Gradle
- SLF4J Logging

### Containerization & Runtime
- Docker
- Podman

---

## 🚀 Running the Application

The application can be run:

- locally (Gradle / IDE)
- in a containerized environment using Podman Compose (recommended)

Environment-specific behavior is controlled via Spring Profiles (dev, prod) and environment variables.


🧩 Prerequisites

- Java 17
- Podman (and Podman Compose or alternatively Docker and Docker Compose)
- A relational database (PostgreSQL recommended via compose)
- An API testing tool (Postman, Insomnia, etc.)

▶️ Local Execution

  1. Clone the repository 
  2. Configure environment variables for database and security credentials3. Run the application:
  3. Run the application 

```bash
./gradlew bootRun
```

The application will be available at:

```bash
http://localhost:8080
```

By default, it is used the dev profile unless overridden via SPRING_PROFILES_ACTIVE.


🐳 Containerized Execution (with Docker or Podman)

The application can be run in a containerized environment using environment-specific ```.env``` files.

Environment Files

* .env.dev → development environment
* .env.prod → production environment

The active profile is selected via SPRING_PROFILES_ACTIVE.

Commands for the shell:

```bash
.\run.ps1 
```

For dev profile.

Or:

```bash
.\run.ps1 -Env "prod"
```

For prod profile.


🔀 Environment Profiles

*dev*

- Intended for local development 
- Automatic schema updates
- Sample data loading enabled
- In-memory users
- SQL logging enabled

*prod*

- Production-like behavior
- Schema validation only
- No sample data
- No in-memory users
- Reduced logging

In production scenarios, credentials are expected to be provided by the runtime environment.

---

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


