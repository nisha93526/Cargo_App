# CargoPro Backend System

A comprehensive cargo and freight management system built with Spring Boot, providing RESTful APIs for managing loads and bookings in the transportation industry.

##  Features

- **Load Management**: Create, update, retrieve, and manage cargo loads
- **Booking System**: Handle transporter bookings for available loads
- **Status Tracking**: Track load and booking statuses throughout the lifecycle
- **Business Rules**: Automated status management based on business logic
- **API Documentation**: Interactive Swagger UI for API exploration
- **Data Validation**: Comprehensive input validation and error handling
- **Database Integration**: PostgreSQL with JPA/Hibernate

##  Technology Stack

- **Framework**: Spring Boot 2.7.18
- **Language**: Java 11
- **Database**: PostgreSQL
- **ORM**: Hibernate/JPA
- **Build Tool**: Gradle
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Validation**: Bean Validation (JSR-303)
- **Utilities**: Lombok

##  Prerequisites

Before running this application, make sure you have the following installed:

- Java 11 or higher
- PostgreSQL 12 or higher
- Gradle 7.0 or higher (or use the included Gradle wrapper)

##  Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/cargopro-backend.git
cd cargopro-backend
```

### 2. Database Setup
Create a PostgreSQL database:
```sql
CREATE DATABASE cargopro_db;
```

### 3. Configure Database Connection
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cargopro_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Build and Run
```bash
# Using Gradle wrapper (recommended)
./gradlew bootRun

# Or using installed Gradle
gradle bootRun
```

The application will start on `http://localhost:8080`

##  API Documentation

Once the application is running, access the interactive API documentation:

**Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

##  Project Structure

```
src/
├── main/
│   ├── java/com/cargoAppService/
│   │   ├── launcher/           # Application entry point
│   │   ├── controller/         # REST controllers
│   │   ├── service/           # Business logic layer
│   │   ├── entities/          # JPA entities
│   │   ├── repositories/      # Data access layer
│   │   ├── dto/              # Data Transfer Objects
│   │   └── exceptions/       # Custom exceptions
│   └── resources/
│       └── application.properties  # Configuration
└── test/                     # Test files
```

##  API Endpoints

### Load Management
- `POST /load` - Create a new load
- `GET /load` - Get all loads (with filtering)
- `GET /load/{id}` - Get load by ID
- `PUT /load/{id}` - Update load
- `DELETE /load/{id}` - Cancel load

### Booking Management
- `POST /booking` - Create a new booking
- `GET /booking` - Get all bookings (with filtering)
- `GET /booking/{id}` - Get booking by ID
- `PUT /booking/{id}` - Update booking status
- `DELETE /booking/{id}` - Delete booking

## Data Models

### Load Entity
```java
{
  "id": "uuid",
  "shipperId": "string",
  "loadingPoint": "string",
  "unloadingPoint": "string",
  "loadingDate": "timestamp",
  "unloadingDate": "timestamp",
  "productType": "string",
  "truckType": "string",
  "noOfTrucks": "integer",
  "weight": "double",
  "comment": "string",
  "status": "POSTED|BOOKED|CANCELLED",
  "datePosted": "timestamp"
}
```

### Booking Entity
```java
{
  "id": "uuid",
  "loadId": "uuid",
  "transporterId": "string",
  "proposedRate": "double",
  "comment": "string",
  "status": "PENDING|ACCEPTED|REJECTED",
  "requestedAt": "timestamp"
}
```

##  Business Rules

1. **Load Status Management**:
   - New loads start with `POSTED` status
   - When a booking is created, load status changes to `BOOKED`
   - Deleting a load sets status to `CANCELLED`

2. **Booking Validation**:
   - Cannot create bookings for cancelled loads
   - Bookings start with `PENDING` status
   - Can be updated to `ACCEPTED` or `REJECTED`

3. **Status Reversion**:
   - If all bookings are deleted/rejected, load status reverts to `POSTED`

##  Testing

Run the test suite:
```bash
./gradlew test
```

##  Monitoring & Logging

- Application logs are configured to show SQL queries
- Hibernate DDL is set to `update` for automatic schema management
- JPA open-in-view is enabled for lazy loading support

##  Deployment

### Production Configuration
For production deployment, update `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=WARN
```
