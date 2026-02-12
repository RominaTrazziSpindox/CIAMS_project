# 📦 CIAMS_project

--- 

CIAMS (Corporate IT Asset Management System) is a Spring Boot-based RESTful application designed for managing an IT inventory, including physical assets, offices, asset types, and software licenses.

The project evolved from a **monolithic architecture** into a **microservices-based system**, following the **Strangler Fig pattern** to gradually extract authentication logic into a dedicated service.


It simulates a real-world enterprise backend, focusing on:

* clean architecture
* explicit business logic
* security
* maintainability


The application allows tracking where assets are located, which licenses are installed on them, and monitoring license expiration.

---

## 📁 Project Structure

```
ciams-project-root/
│
├── auth-service/ # Identity microservice (MongoDB + JWT)
│ ├── src/
│ ├── build.gradle
│ └── Dockerfile
│
├── inventory-service/ # Business domain microservice (PostgreSQL)
│ ├── src/
│ ├── build.gradle
│ └── Dockerfile
│
├── infrastructure/ # Container orchestration
│ ├── docker-compose.yml
│ ├── docker-compose-db.yml
│ └── run.ps1
│
├── docs/ # Project documentation
│
├── README.md
└── README-2nd-Version.md
```

Each service is an independent Gradle project.

---

## 🏗️  System Architecture

The system is composed of two independent services communicating via HTTP and JWT:

```

Client (Postman)
   ↓
   
auth-service  →  MongoDB
   ↓  (JWT)
   
inventory-service  →  PostgreSQL
```

### Microservices

🔐 **auth-service (Identity Provider)**

- Manages user registration and authentication 
- Issues signed JWT tokens 
- Uses MongoDB 
- Stateless authentication

Stores:

- username (unique)
- password (BCrypt hashed)
- roles (e.g. USER, ADMIN)

📦 **inventory-service (Business Domain)**

- Manages assets, offices, asset types, and software licenses 
- Uses PostgreSQL (relational)
- Validates JWT tokens issued by auth-service 
- Does not manage users 
- Does not store credentials 
- Stateless per request

This separation enforces clear responsibility boundaries between identity management and business logic.

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
    - Mirrors the relational database schema

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
    - JWT validation filter
    - Manages security-related error responses (401 Unauthorized, 403 Forbidden)

- **Utils**
    - Input normalization utilities for clean and consistent data (e.g. lowercase keys, trimmed values)

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
* JWT Authentication
* Read operations publicly accessible
* Write operations restricted to authenticated users
* Custom authentication entry point and access denied handler

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

* Registration and authentication flow
* CRUD operations
* License installation/uninstallation
* Role-based access
* Error scenarios


### **MAIN ENDPOINTS**

### Registration Management

- `POST /auth/register`

### Login Management

- `POST /auth/login`

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

## End-To-End Flow (Postman Scenario)

1. Login via auth-service 
2. Store JWT in environment variable {{jwt_token}}
3. Call inventory-service endpoints using:

```Authorization: Bearer {{jwt_token}}```

This demonstrates:

* Separation of concerns
* Stateless authentication
* Cross-service trust via shared secret 

---

## 🛠️ Tech Stack

### Auth-Service
- Java 17
- Spring Boot 3.5
- Spring Security
- Spring Data MongoDB
- JWT (jjwt)
- BCrypt
- MongoDB
- Gradle

### Inventory-Service
- Java 17
- Spring Boot 3.5
- Spring Data JPA / Hibernate
- Spring Security
- MapStruct
- Lombok
- Jakarta Validation
- PostgreSQL
- SLF4J Logging
- Caffeine
- Gradle

### Containerization & Runtime
- Docker (with also Docker Compose)
- Podman (with also Podman Compose)

---

## 🚀 Running the System

CIAMS can be executed in two ways:

- Local development mode (recommended during development)
- Fully containerized mode (recommended otherwise)

Environment-specific behavior is controlled via Spring Profiles (`dev`, `prod`) and environment variables.

🧩 Prerequisites

- Java 17
- Podman (and Podman Compose or alternatively Docker and Docker Compose)
- A relational database (PostgreSQL recommended via compose)
- An API testing tool (Postman, Insomnia, etc.)

▶️ Local Execution

1. Clone the repository
2. Configure environment variables for database and security credentials.

In local mode, only the databases are containerized.
The Spring Boot services are executed directly from the IDE or via Gradle.

3. From the `infrastructure` folder:

```bash
podman-compose -f docker-compose-db.yml up -d
```

This will start:
 * MongoDB (used by auth-service) at port: 27017
 * PostgreSQL (used by inventory-service) at port: 5432

4. From the `auth-service ` directory:

```./gradlew bootRun```

The application will be available at:

```http://localhost:9091```

5. From the `inventory-service ` directory:

```./gradlew bootRun```

The application will be available at:

```http://localhost:9090```


By default, it is used the dev profile unless overridden via SPRING_PROFILES_ACTIVE.


🐳 Fully Containerized Execution

To start the entire system (databases + services) in containers.
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

In both cases this will start:

* MongoDB
* PostgreSQL
* auth-service
* inventory-service

🔀 Environment Profiles

The system supports two Spring profiles: `dev` and `prod`.
Profiles apply independently to both services (`auth-service` and `inventory-service`).

*dev*

Intended for local development and testing.

- Automatic schema updates (PostgreSQL)
- Debug-friendly configuration
- SQL logging enabled (inventory-service)
- Detailed logging enabled
- Suitable for local containerized databases

*prod*

Production-oriented configuration.

- Schema validation only (no automatic updates)
- Reduced logging
- No sample data initialization
- Strict configuration requirements

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


