# Enterprise Order Management System (OMS)

A secure, performance-optimized Java backend REST API engine engineered for high-throughput product cataloging, transactional shopping checkouts, and dynamic multi-tiered access control rules.

## 🚀 Technology Stack
* **Runtime / Engine:** Java 17, Spring Boot 4.0.7
* **Data Layer:** Spring Data JPA, Hibernate ORM, MySQL
* **Security Layer:** Spring Security (Stateless Authorization Filter), JWT (JJWT 0.12.6), BCrypt
* **Tools / Validation:** Jakarta Validation, Maven, Docker

## 🛠️ System Architecture & Code Flow
The system processes data linearly using industry-standard decoupled architecture design:
`Client Request` ➔ `Security/JWT Filter` ➔ `REST Controller` ➔ `Service Layer (Business Logic)` ➔ `Data Access Layer (JPA Repository)` ➔ `MySQL Database`

## 🔒 Security Matrix & Endpoints

| HTTP Method | API Endpoint | Role Authorization | Purpose |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/users/register` | Public (`PermitAll`) | Creates a new user profile with BCrypt hashing |
| **POST** | `/api/users/login` | Public (`PermitAll`) | Verifies password & issues a secure stateless JWT |
| **POST** | `/api/products` | `ADMIN` Only | Ingests a new enterprise product with a unique SKU |
| **GET** | `/api/products` | `ADMIN`, `CUSTOMER` | Fetches full dynamic product catalog availability |
| **POST** | `/api/orders` | `CUSTOMER` Only | Executes standard transactional checkout & stock deduction |
| **GET** | `/api/orders` | `CUSTOMER` Only | Pulls historical user-authenticated purchase context |


## ⚙️ Core Technical Highlights
* **Atomic Transactions:** Checkout sequence leverages `@Transactional` layers, ensuring matching integrity between total invoice sums and warehouse inventory deductions. If any single validation step or inventory ceiling boundary checks fail, the entire execution cascades back to zero.
* **Price Isolation:** Stores product price snapshots directly inside order line rows (`order_items`) at the instant of calculation to guard historical fiscal analytics against future admin inventory updates.
