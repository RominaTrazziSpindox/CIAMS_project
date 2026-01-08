# CIAMS_project
Corporate IT Asset Management System project: a RESTful backend for managing physical offices, hardware assets, and software licenses, with a focus on traceability and compliance.

This project aims to develop a robust and scalable RESTful backend that enables:
* Cataloging physical company locations (Offices)
* Standardizing hardware asset types
* Tracking individual devices and their physical location
* Managing software licenses installed on devices to ensure legal compliance

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
    - Handles HTTP routing and request/response mapping
    - Contains no business logic

- **Service**
    - Implements business rules and domain logic
    - Performs validations and consistency checks
    - Coordinates repositories and domain entities

- **Repository**
    - Handles data access using Spring Data JPA
    - Contains no business logic

- **Entity**
    - Represents the persistent domain model
    - Defines JPA mappings and relationships

- **DTO (Request / Response)**
    - Defines the API contract
    - Prevents exposing JPA entities directly

- **Mapper**
    - Converts between Entities and DTOs
    - Implemented using MapStruct

- **Exception**
  - Centralizes application-wide error handling
  - Maps exceptions to appropriate HTTP status codes
  - Ensures a consistent JSON error response format across the API
  - Keeps controllers and services free from error-handling logic

- **Security**
  - Handles authentication and authorization concerns
  - Enforces access control rules before requests reach controllers
  - Manages security-related error responses (401 Unauthorized, 403 Forbidden)
  - Decouples access control logic from business logic
---

## 🧱 Domain Model

### Core Entities

#### Office
Represents a physical company location.

- `id`
- `name` (unique)

#### AssetType
Defines a standardized hardware category.

- `id`
- `assetTypeName`
- `assetTypeDescription`

#### Asset
Represents a physical device owned by the company.

- `id`
- `serialNumber` (unique)
- `purchaseDate`
- `office` (Many-to-One)
- `assetType` (Many-to-One)
- `softwareLicenses` (Many-to-Many)

#### SoftwareLicense
Represents a purchased software license.

- `id`
- `softwareName` (unique)
- `expirationDate`
- `maxInstallations` (nullable, null = unlimited)
- `installedAssets` (Many-to-Many)

---

## 📌 Design Principles

- No business logic in controllers
- No domain logic in repositories
- Clear separation between API and persistence models
- Explicit, readable code over hidden magic
- Domain rules enforced centrally

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

### Asset Management

- `GET /assets/all`
- `GET /assets/{id}`
- `GET /assets/serial/{serialNumber}`
- `POST /assets/insert`
- `PUT /assets/update/{id}`
- `PUT /assets/move/{assetId}?officeId={officeId}`
- `DELETE /assets/{id}`

---

### Software License Management

#### CRUD Operations

- `GET /software-licenses/all`
- `GET /software-licenses/{id}`
- `POST /software-licenses/insert`
- `PUT /software-licenses/update/{id}`
- `DELETE /software-licenses/{id}`

#### Compliance Operations

- **Install software on an asset**

- `POST /software-licenses/{licenseId}/install/{assetId}`

- **Uninstall software from an asset**

- `DELETE /software-licenses/{licenseId}/uninstall/{assetId}`

- **Audit installed software on an asset**

- `GET /software-licenses/asset/{assetId}`

- **Retrieve licenses expiring in the next 30 days**

- `GET /software-licenses/expiring-soon`


---

## 🧪 Validation & Error Handling

- Request validation is handled using Jakarta Bean Validation (`@Valid`)
- Business rule violations are enforced in the service layer
- Errors are centralized using `@RestControllerAdvice`
- The API returns meaningful HTTP status codes and error messages
- Error responses follow a unified JSON structure
- Each error response includes a human-readable action hint to guide API consumers
- Error handling is fully decoupled from controllers and services (with class `GlobalExceptionHandler`)

---

## 🔐 Security & Access Control

The application is secured using **Spring Security** with **HTTP Basic Authentication**, designed to protect write operations while keeping read operations publicly accessible.

### Authentication

- Basic Authentication is enabled
- Credentials are required for all write operations
- Authentication is handled at the security filter level (before controllers)

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
- **Spring Boot 3.8**
- **Spring Data JPA / Hibernate**
- **MapStruct**
- **Lombok**
- **Jakarta Validation**
- **PostgreSQL** (configurable)
- **Maven**
- **Spring Security**

---

## 🚀 Getting Started

1. Clone the repository
2. Configure the database in `application.yml`
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
CREATE DATABASE ciams;
```

Update the database configuration in src/main/resources/application.yml (or application.properties):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ciams
    username: your_db_username
    password: your_db_password

jpa:
  hibernate:
    ddl-auto: update
  show-sql: true
  properties:
    hibernate:
      format_sql: true
```

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


## 📄 License

This project is intended for educational and professional demonstration purposes.


---

## 👤 Author - Romina Trazzi
