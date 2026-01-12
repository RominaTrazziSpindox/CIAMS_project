# CIAMS_project
CIAMS is a RESTful backend application designed to manage corporate IT assets, physical offices, and software licenses, with a strong focus on traceability, consistency, and compliance.

The system supports the full lifecycle of hardware devices and software licenses, enabling organizations to maintain a clear and auditable inventory of their IT resources.

CIAMS aims to provide a robust and scalable backend that allows:

* Cataloging physical company locations (Offices)
* Standardizing hardware categories (Asset Types)
* Tracking individual devices (Assets) and their physical location
* Managing software licenses installed on devices
* Enforcing software compliance rules server-side
* Offering both lightweight list views and detailed asset inspections

---

## 🏗️ Architecture Overview

The application follows a **layered architecture**, with strict separation of concerns:


Controller → Service → Repository → Database

                ↓
                Mapper
                
                ↓
                DTO

### Layer Responsibilities

- **Controller**
    - Exposes REST endpoints
    - Performs input validation (@Valid)

- **Service**
    - Implements business rules and domain logic
    - Coordinates repositories and domain entities
    - Performs validations and consistency checks
    - Handles HTTP routing and request/response mapping
    - Orchestrates complex operations (e.g. moving assets, installing licenses)

- **Repository**
    - Handles data access using Spring Data JPA
    - Provides query methods based on domain concepts

- **Entity**
    - Represents the persistent domain model
    - Defines JPA mappings and relationships
    - Mirrors the logical database schema

- **DTO (Request / Response)**
    - Defines the API contract
    - Prevents exposing JPA entities directly
    - Uses human-friendly keys (names, serial numbers) instead of internal IDs

- **Mapper**
    - Converts between Entities and DTOs
    - Implemented using MapStruct

- **Exception**
  - Centralized via `@RestControllerAdvice`
  - Maps exceptions to appropriate HTTP status codes
  - Ensures a consistent JSON error response format across the API

- **Security**
  - Implemented with Spring Security
  - Manages security-related error responses (401 Unauthorized, 403 Forbidden)
 
- **Utils**
  - Provides shared utility logic used across the application
  - Centralizes text normalization and input sanitization
  - Ensures consistent and human-friendly data persistence (e.g. lowercase keys, trimmed values)

---

## 🧱 Domain Model

### Core Entities

#### Office
Represents a physical company location.

- `id`
- `name` (unique, domain key)

#### AssetType
Defines a standardized hardware category.

- `id`
- `assetTypeName` (unique, domain key)
- `assetTypeDescription` (optional)

#### Asset
Represents a physical device owned by the company.

- `id`
- `serialNumber` (unique, domain key)
- `purchaseDate`
- `office` (Many-to-One)
- `assetType` (Many-to-One)
- `softwareLicenses` (Many-to-Many)

#### SoftwareLicense
Represents a purchased software license.

- `id`
- `softwareName` (unique, domain key)
- `expirationDate`
- `maxInstallations` (nullable, null = unlimited)
- `installedAssets` (Many-to-Many)

---

## 📌 Design Principles

- No business logic in controllers
- No domain logic in repositories
- Clear separation between API and persistence models
- Domain rules enforced centrally
- Controlled caching applied only to stable, read-heavy operations
- Human-friendly data handling through centralized normalization and validation
- Explicit separation between list views and detailed views

---

## 🔐 Software License Compliance Rules

Software license management is a core feature of CIAMS.

The system enforces the following rules at the **service layer**:

- A license cannot be installed on the same asset more than once
- A license cannot be installed if it is expired
- A license cannot exceed its maximum number of allowed installations
- Installation and uninstallation operations update both sides of the relationship
- All compliance rules are enforced server-side

---

## 🔌 REST API Overview

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


### Software Licence Management

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

## 🧪 Validation & Error Handling

- Request validation is handled using Jakarta Bean Validation (`@Valid`)
- Input data is validated and normalized before business rule enforcement
- Business rule violations are enforced in the service layer
- Errors are centralized using `@RestControllerAdvice`
- Meaningful HTTP status codes and error messages
- Error responses follow a unified JSON structure
- Each error response includes a human-readable action hint to guide API consumers

---

## 🔐 Security & Access Control

The application is secured using **Spring Security** with **HTTP Basic Authentication**.

### Authentication

- Basic Authentication is enabled
- Credentials are required for all write operations
- Authentication is handled at the security filter level (before controllers)
- Authentication credentials are externalized using environment variables and loaded at runtime, avoiding hard-coded secrets in the codebase.

### Authorization Rules

- **DELETE operations** require the `ADMIN` role
- **POST / PUT operations** require authentication
- **GET operations** are publicly accessible

Authorization is enforced using method and path-based rules, ensuring clear and predictable access control.

### Security Error Handling

Security-related errors are handled separately from application errors:

- **401 Unauthorized**
  - Triggered when authentication credentials are missing or invalid
  - Returned before reaching controllers
- **403 Forbidden**
  - Triggered when an authenticated user lacks sufficient permissions

Both errors return a structured JSON response using the same error DTO format as the rest of the application, ensuring consistency across all API responses.

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.5**
- **Spring Data JPA / Hibernate**
- **MapStruct**
- **Lombok**
- **Jakarta Validation**
- **PostgreSQL** 
- **Maven**
- **Spring Security**
- **Spring Cache**

---

## 🚀 Getting Started

1. Clone the repository
2. Configure environment variables for database and security credentials 
3. Run the application
4. Test endpoints using Postman or similar tools

---

## ⚙️ Setup & Run

*Prerequisites* 

Make sure the following tools are installed on your system:

- **Java 17 (or compatible LTS version)**
- **Maven 3.8+**

- A relational database:

  - **PostgreSQL (recommended) or MySQL**
  - **An API testing tool (Postman, Insomnia, etc.)**

*Database Configuration*

Create a database for the application (example with PostgreSQL):

```sql
CREATE DATABASE inventory_db;
```

Configure database connection using environment variables loaded from a `.env` file:

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=inventory_db
DB_USER=your_db_username
DB_PASSWORD=your_db_password
```

The `.env` file is automatically loaded at startup and must not be committed to version control.

In application.yaml (or application.properties) use placeholders:

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

Security credentials (Basic Authentication) are also configured via environment variables.

*Build the Project*

From the project root directory, run:

```bash
mvn clean install
```

This will:

- compile the project
- run validations
- generate MapStruct mappers
- package the application

*Run the Application*

Option 1: Using Maven: 

```bash
mvn spring-boot:run
```

Option 2: Using the JAR: 

```bash
java -jar target/ciams-*.jar
```

*Application Startup*

Once started, the application will be available at: http://localhost:8080

Example endpoint: http://localhost:8080/offices/all

---

### 🔮 Future Improvements

While CIAMS already provides a complete and consistent backend for IT asset and software license management, several enhancements could further improve scalability, usability, and maintainability.

1. Pagination & Sorting for List Endpoints: 
Currently, list endpoints return full collections.
A future improvement would introduce:

- Pagination (Pageable)

Sorting by key fields (e.g. asset type, purchase date, expiration date).
This would improve performance and usability when dealing with large datasets.

2. Advanced Search & Filtering

Read operations could be extended with more advanced filtering capabilities, such as:

- Assets filtered by purchase date range
- Assets grouped by office or asset type 
- Licenses nearing expiration within a configurable time window

These features would enable more powerful reporting and auditing use cases.

3. Advanced Search & Filtering

Read operations could be extended with more advanced filtering capabilities, such as:

- Assets filtered by purchase date range
- Assets grouped by office or asset type
- Licenses nearing expiration within a configurable time window

These features would enable more powerful reporting and auditing use cases.

4. API Documentation (OpenAPI / Swagger)

The API could be documented using OpenAPI specifications and Swagger UI to:

- Provide interactive API documentation
- Improve discoverability for API consumers
- Simplify integration with external systems

5. Frontend & Visualization Layer

Although CIAMS is currently backend-focused, a future extension could include:

- A web-based frontend
- Dashboards for asset distribution and license compliance
- Visual tools for auditing and reporting

This would improve usability for non-technical users.

## 📄 License

This project is intended for educational and professional demonstration purposes.


---

## 👤 Author - Romina Trazzi
