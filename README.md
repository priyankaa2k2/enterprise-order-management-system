# Enterprise Order Management System (OMS)

A Java/Spring Boot backend application for managing users, products, inventory, and customer orders, with authentication, authorization, transactional business logic, and database persistence

## 🚀 Technology Stack
* **Runtime / Engine:** Java 17, Spring Boot 4.0.7
* **Data Layer:** Spring Data JPA, Hibernate ORM, MySQL
* **Security Layer:** Spring Security (Stateless Authorization Filter), JWT (JJWT 0.12.6), BCrypt
* **Tools / Validation:** Jakarta Validation, Maven, Docker

## 🛠️ System Architecture & Code Flow
The system processes data linearly using industry-standard decoupled architecture design:
`Client Request` ➔ `Security/JWT Filter` ➔ `REST Controller` ➔ `Service Layer (Business Logic)` ➔ `Data Access Layer (JPA Repository)` ➔ `MySQL Database`

Supporting components include:

* DTOs for API request/response models
* Entity classes for persistence
* Security configuration and JWT authentication
* Global exception handling
* Validation
* Transaction management

## 🔐 Security

The application uses **Spring Security and JWT** for authentication and authorization.

Key security concepts implemented:

* User authentication
* Password protection
* JWT generation and validation
* Role-based access control
* Protected API endpoints

## 🔒 Security Matrix & Endpoints

| HTTP Method | API Endpoint | Role Authorization | Purpose |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/users/register` | Public (`PermitAll`) | Creates a new user profile with BCrypt hashing |
| **POST** | `/api/users/login` | Public (`PermitAll`) | Verifies password & issues a secure stateless JWT |
| **POST** | `/api/products` | `ADMIN` Only | Ingests a new enterprise product with a unique SKU |
| **GET** | `/api/products` | `ADMIN`, `CUSTOMER` | Fetches full dynamic product catalog availability |
| **POST** | `/api/orders` | `CUSTOMER` Only | Executes standard transactional checkout & stock deduction |
| **GET** | `/api/orders` | `CUSTOMER` Only | Pulls historical user-authenticated purchase context |

## 📦 Order Processing

Order processing demonstrates transactional business logic.

The order workflow includes:

1. Validate the request
2. Verify product availability
3. Check inventory
4. Create the order
5. Deduct inventory
6. Persist the transaction

Transactional boundaries help ensure that related database operations are completed consistently.

## 🗄️ Database

The application uses **MySQL** with JPA/Hibernate for persistence.

Main domain areas include:

* Users
* Roles
* Products
* Inventory
* Orders
* Order items

Entity relationships are managed using JPA/Hibernate mappings.

## 📚 API Documentation

API endpoints are documented using **Swagger / OpenAPI**.

After starting the application, the API documentation can be accessed through the configured Swagger UI endpoint.

## ▶️ Running Locally

### Prerequisites

* Java 17
* Maven
* MySQL

### 1. Clone the repository

```bash
git clone https://github.com/priyankaa2k2/enterprise-order-management-system.git
cd enterprise-order-management-system
```

### 2. Configure the database

Create a MySQL database and configure the required connection properties in the application configuration.

### 3. Build the application

```bash
./mvnw clean install
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The application will start using the configured Spring Boot port.

## 🧪 Testing

The REST APIs are currently being tested using Postman.

A dedicated Postman workspace is maintained with saved requests covering authentication, product management, and order workflows.

## 📚 Project Purpose

This project was built to demonstrate practical backend development using the **Java and Spring Boot ecosystem**, including REST API development, security, persistence, database design, validation, and transactional business logic.

## 📌 Project Status

The core backend functionality is implemented and currently being validated through API testing.

Future improvements may include:

* Unit and integration testing with JUnit/Mockito
* Docker-based application setup
* Additional API functionality
* Expanded automated test coverage

These will be added to the project as they are implemented and validated.
