# Order Management System

A backend-focused Order Management System built using Spring Boot and a microservices architecture.

The project demonstrates REST API development, database persistence, service-to-service communication using OpenFeign, API Gateway routing, validation, exception handling, pagination, sorting, order status management, and product stock management.

## Architecture

```text
                         Client

                           |

                           v

                  +-------------------+
                  |    API Gateway    |
                  |       :8080       |
                  +---------+---------+
                            |
                 +----------+----------+
                 |                     |
                 v                     v
        +----------------+    +----------------+
        | Order Service  |    | Product Service|
        |     :8081      |    |     :8082      |
        +-------+--------+    +----------------+
                |
                | OpenFeign
                |
                v
        Product Service :8082
```

### Services

| Service         | Port | Responsibility               |
| --------------- | ---: | ---------------------------- |
| API Gateway     | 8080 | Request routing              |
| Order Service   | 8081 | Order management             |
| Product Service | 8082 | Product and stock management |

## Technologies

* Java 21
* Spring Boot 4.1.1
* Spring Cloud Gateway
* Spring Cloud OpenFeign
* Spring Data JPA
* REST APIs
* PostgreSQL
* Maven
* Jakarta Bean Validation
* Git
* Apache HttpClient 5

## Project Structure

```text
order-management/

│
├── api-gateway/
│   └── src/
│
├── order-service/
│   └── src/
│
├── product-service/
│   └── src/
│
├── .gitignore
└── README.md
```

## Main Features

### Order Service

* Create orders
* Get all orders
* Get order by ID
* Pagination
* Sorting
* Update order status
* Validate order status transitions
* Cancel orders
* Validate product existence
* Check product stock
* Reduce product stock when an order is created
* Global exception handling
* Request validation
* Service-to-service communication using OpenFeign

### Product Service

* Create products
* Get all products
* Get product by ID
* Update products
* Delete products
* Update product stock
* Insufficient stock validation
* Global exception handling
* Request validation

### API Gateway

* Central entry point for client requests
* Route order requests to Order Service
* Route product requests to Product Service

## API Gateway Routes

### Orders

```text
/api/orders/**
        |
        v
http://localhost:8081
```

Example:

```bash
curl http://localhost:8080/api/orders
```

### Products

```text
/api/products/**
        |
        v
http://localhost:8082
```

Example:

```bash
curl http://localhost:8080/api/products
```

## Database

The application uses separate PostgreSQL databases for the services.

```text
Order Service
     |
     v
 PostgreSQL
 order_db


Product Service
     |
     v
 PostgreSQL
 products_db
```

Each microservice owns its own database.

## Running the Project

Run each service separately.

### 1. Start Product Service

```bash
cd product-service
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8082
```

### 2. Start Order Service

Open another terminal:

```bash
cd order-service
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8081
```

### 3. Start API Gateway

Open another terminal:

```bash
cd api-gateway
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8080
```

## Testing

External API requests should normally go through the API Gateway.

### Get Products

```bash
curl http://localhost:8080/api/products
```

### Get Orders

```bash
curl http://localhost:8080/api/orders
```

### Create a Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Keyboard",
    "description": "Bluetooth mechanical keyboard",
    "price": 15000.00,
    "stockQuantity": 50
  }'
```

### Create an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 101,
    "productId": 1,
    "quantity": 2
  }'
```

When an order is created:

```text
Client
  |
  v
API Gateway
  |
  v
Order Service
  |
  | OpenFeign
  v
Product Service
  |
  | Check stock
  | Reduce stock
  |
  v
Order Service
  |
  v
Save Order
```

## Order Status

The supported order statuses are:

```text
PENDING
CONFIRMED
CANCELLED
COMPLETED
```

Valid transitions:

```text
PENDING
   |
   +----> CONFIRMED
   |          |
   |          +----> COMPLETED
   |          |
   |          +----> CANCELLED
   |
   +----> CANCELLED
```

`CANCELLED` and `COMPLETED` are final states.

## Service-to-Service Communication

Order Service communicates directly with Product Service using **Spring Cloud OpenFeign**.

```text
Order Service :8081
       |
       | OpenFeign
       |
       v
Product Service :8082
```

The API Gateway is **not** used for internal service-to-service communication.

Feign provides a declarative HTTP client interface for communication between the services.

The Product Service URL is configured in the Order Service:

```properties
product-service.url=http://localhost:8082
```

## Important Design Principles

### Database per Service

Each microservice has its own database.

```text
Order Service   -> order_db

Product Service -> products_db
```

Services do not directly access another service's database.

### Separation of Responsibilities

```text
API Gateway
    |
    | Routing
    v
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
Database
```

### Business Logic

Business logic is kept inside the service layer rather than controllers.

### Declarative Service Communication

OpenFeign is used to simplify service-to-service HTTP communication.

```text
Order Service
     |
     | Feign Interface
     v
Product Service API
```

## Learning Goals

This project is designed to practice:

* Spring Boot
* REST API development
* Spring Data JPA
* PostgreSQL
* DTOs
* Validation
* Exception handling
* Microservices
* Service-to-service communication
* Spring Cloud OpenFeign
* API Gateway
* Clean project structure
* Git and Maven

## Author

Backend-focused learning project built with Java and Spring Boot.
