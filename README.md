## Employee Management System

This is a backend application for an employee management system that is designed. Uses a microservices-based architecture.

The system consists of five microservices:
- **Discovery Service** - for service registration and discovery
- **API Gateway** - for routing requests and applying security
- **Config Server + Shared Config Repo** - externalized configuration for all services
- **Authentication Service** - handles user login and role-based control
- **Employee Management Service** - for managing employee and departmental data

### Features
- Employee Management
  - An Admin can add, update, delete and view employee details
  - A Manager can view employees in their department
  - An employee can view their own details.

- Department Management
  - An Admin can add, update, delete, and view departmental details

### Technologies used
- Spring Boot (Java 21)
- Spring Security (JWT + RBAC)
- Spring cloud
- Spring Data JPA
- PostgreSQL (with Flyway for migrations)
- Eureka for discovery
- Swagger for API documentation
- Kafka for event streaming.

### How to use
- Prerequisites
  - Have PostgreSQL installed.
  - Have Kafka installed. Make sure it's reachable from localhost:9092
- Start the config server and Service discovery
- Start kafka server.
- Start the remaining services api-gateway, auth-service and employee-service
- You can log in at `GET localhost:8222/api/v1/departments` using the following details:
  - As an admin - email: `admin123@email.com`, password: `Pass1234`
  - As a manager - email: `manager123@email.com`, password: `Pass1234`
  - As a user - email: `user123@email.com`, password: `Pass1234`
- For Swagger API docs go to:
  - `localhost:8081/swagger-ui/index.html` for the authentication service
  - `localhost:8082/swagger-ui/index.html` for the employee management service

Note: The gateway runs in port 8222 and routes all requests route to the respective microservices. It assumes you always go through the gateway.
When deleting a department, to avoid cascading issues, the system would only perform the operation if there are no employees in it.

### Sample Request/Response
- View all departments
  - Request: `GET localhost:8222/api/v1/departments` (must be logged in as an admin. Insert the bearer token in the authorization header)
  - Response:
    ```
    [
      {
        "id": 2,
        "name": "devops",
        "description": "This department is responsible for platform reliability and scaling",
        "numberOfEmployees": 30
      },
      {
        "id": 2,
        "name": "customer service",
        "description": "This department is responsible for attending to needs of end users",
        "numberOfEmployees": 18
      }
    ]
    ```
- View employees in my department
  - Request `GET localhost:8222/api/v1/employees/my-department` (logged in as a manager)
  - Response:
    ```
    [
      {
        "name": "John Smith",
        "email": "john@example.com",
        "phone": "+122345677",
        // other data
      },
      {
        "name": "David Paul",
        "email": "david@example.com",
        "phone": "+99887377",
        // other data
      },
      // ...
    ]
    ```

