# Warehouse REST API

The Warehouse REST API is a Spring Boot microservice for managing warehouse inventory. It provides a RESTful interface for tracking parts by their unique composite identifiers (material number, serial number, and supplier number) along with their quantities.
## Features
- CRUD operations for parts management
- Increasing and decreasing part quantities
- Validation to prevent negative quantities
- Comprehensive logging with daily log rotation
- API documentation with OpenAPI/Swagger
- Extensive test coverage with unit and integration tests
## System Requirements
- Java 17 or higher
- PostgreSQL database (or H2 for development)
- Maven for building and managing dependencies
## Backend technology

- Java 17
- Spring 
- Maven
- REST API
- PostgreSQL
- H2 Database
- SLF4J
- Mockito / JUnit 5
## Getting Started

Database Configuration
By default, the application uses PostgreSQL. 

You can modify the database connection in the application.properties file:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/warehouse
spring.datasource.username=postgres
spring.datasource.password=your_password
```

For development purposes, you can uncomment the H2 in-memory database configuration instead.
## Swagger
![App Screenshot](https://private-user-images.githubusercontent.com/115643106/434003268-6346a341-ef43-4e1c-a749-741ba54f2a0a.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NDQ3NDgzNzMsIm5iZiI6MTc0NDc0ODA3MywicGF0aCI6Ii8xMTU2NDMxMDYvNDM0MDAzMjY4LTYzNDZhMzQxLWVmNDMtNGUxYy1hNzQ5LTc0MWJhNTRmMmEwYS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwNDE1JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDQxNVQyMDE0MzNaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1kYzZiMzUxNGQwNGI5ODdlNTc0MDU2NTAyYjQ4N2JhMDJiZTIxZmMzNjkyYmFjYjhlODZmZTYzZDc5MWFmZjEzJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.LB6igR7OXVtKwEeNxOsIFy9sSkaxvbPDoidzodlSwZ8)
## API documentation

Once the application is running, you can access the Swagger UI documentation at:

http://localhost:8080/swagger-ui/index.html

The OpenAPI specification is available at:

http://localhost:8080/api-docs
## API Endpoints

- `GET /api/v1/parts` - Get all parts in the warehouse
- `GET /api/v1/parts/{materialNumber}/{serialNumber}/{supplierNumber}` - Get a specific part
- `POST /api/v1/parts` - Add a new part (initial quantity set to 0)
- `DELETE /api/v1/parts/{materialNumber}/{serialNumber}/{supplierNumber}` - Delete a part (only if quantity is 0)
- `PATCH /api/v1/parts/{materialNumber}/{serialNumber}/{supplierNumber}/add?amount={value}` - Increase part quantity
- `PATCH /api/v1/parts/{materialNumber}/{serialNumber}/{supplierNumber}/subtract?amount={value}` - Decrease part quantity
## Data Model

Part:

- `Composite ID` (materialNumber, serialNumber, supplierNumber)
- `Quantity` (non-negative integer)

`Example JSON`
```json
{
  "id": {
    "materialNumber": "MAT100",
    "serialNumber": "SER100",
    "supplierNumber": "SUP100"
  },
  "quantity": 10
}
```
## Testing
The application includes both unit tests and integration tests:
- Unit tests use Mockito to test service layer logic without database dependencies
- Integration tests use TestRestTemplate to perform end-to-end testing with an actual database
## Logging

Logs are stored in the `logs` directory with the following configuration:
- Daily log rotation with 30-day retention
- SQL statements and parameters are logged at DEBUG/TRACE level
- Application events are logged at INFO level
## License

This project is licensed under the Apache License 2.0.
