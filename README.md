# Enterprise Order Management System (OMS)

A production-oriented Java backend application for managing users, products, inventory, and customer orders with secure authentication, role-based authorization, transactional order processing, automated testing, and Docker-based deployment.

## 🚀 Tech Stack

- **Backend:** Java 17, Spring Boot 4.0.7
- **Security:** Spring Security, JWT, BCrypt
- **Database:** MySQL, Spring Data JPA, Hibernate
- **API:** REST APIs, Swagger / OpenAPI
- **Testing:** JUnit 5, Mockito, MockMvc, H2, Postman
- **Build:** Maven
- **DevOps:** Docker, Docker Compose
- **Version Control:** Git, GitHub

## ✨ Key Features

### User & Security
- User registration and login
- BCrypt password hashing
- JWT-based stateless authentication
- `ADMIN` and `CUSTOMER` role-based authorization
- Protected REST endpoints
- Authenticated user profile

### Product & Inventory
- Create products with unique SKU
- Retrieve product catalog
- Inventory availability validation
- Automatic stock deduction during order creation

### Order Management
- Create orders with multiple products
- Automatic order total calculation
- Customer order history
- Transactional order processing
- Admin-controlled order status updates
- Strict order lifecycle validation

### Order Lifecycle

```text
PENDING → CONFIRMED → SHIPPED → DELIVERED
    ↓          ↓
CANCELLED   CANCELLED
```

Invalid status transitions are rejected through business-rule validation.

## 🏗️ Architecture

The application follows a layered backend architecture:

```text
Client
  ↓
Spring Security / JWT Filter
  ↓
REST Controller
  ↓
DTO & Validation
  ↓
Service / Business Logic
  ↓
Spring Data JPA Repository
  ↓
Hibernate
  ↓
MySQL
```

Global exception handling provides consistent API error responses for validation failures, missing resources, duplicate resources, insufficient stock, invalid credentials, and invalid order status transitions.

## 🔗 Main API Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/users/register` | Public | Register user |
| POST | `/api/users/login` | Public | Login and receive JWT |
| GET | `/api/users/profile` | Authenticated | Get user profile |
| POST | `/api/products` | ADMIN | Create product |
| GET | `/api/products` | ADMIN / CUSTOMER | View products |
| POST | `/api/orders` | CUSTOMER | Create order |
| GET | `/api/orders` | CUSTOMER / ADMIN | View order history |
| PATCH | `/api/orders/{id}/status` | ADMIN | Update order status |

## 🧪 Testing

The project includes automated tests covering:

- Service-layer business logic
- Controllers using MockMvc
- Authentication and authorization
- JWT-protected endpoints
- Product and order workflows
- Inventory validation
- Order lifecycle rules
- Exception scenarios

**Current test result:**

```text
Tests run: 44
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Postman API tests are also maintained in the `postman/` directory.

## 📚 Swagger / OpenAPI

Interactive API documentation is available after starting the application:

```text
http://localhost:8080/swagger-ui/index.html
```

JWT-protected APIs can be tested directly through Swagger using the authorization option.

## 🐳 Run with Docker

### Prerequisites

- Docker Desktop
- Docker Compose

Create a `.env` file using the provided `.env.example`.

Then start the complete application:

```bash
docker compose up --build
```

Docker Compose starts:

- Spring Boot OMS application
- MySQL database
- Persistent MySQL volume
- Container networking
- Database health check

Application:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

Stop the application:

```bash
docker compose down
```

## 🔐 Environment Configuration

Sensitive configuration is supplied through environment variables instead of being committed to Git.

Example:

```env
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_secure_jwt_secret
JWT_EXPIRATION=3600000
```

The actual `.env` file is excluded through `.gitignore`.

## 📂 Project Structure

```text
src/main/java/.../
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
├── security
└── service
```

## 📌 Project Status

Core backend development is complete and includes:

**REST APIs • JWT Security • Role-Based Authorization • MySQL Persistence • Order Lifecycle Rules • Global Exception Handling • Swagger Documentation • Automated Testing • Docker & Docker Compose**

The project is built as a practical demonstration of production-oriented Java backend development using the Spring Boot ecosystem.
