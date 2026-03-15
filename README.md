# Event Booking System API

A RESTful backend API for an Event Booking System built with Java Spring Boot. Supports two user roles — **Event Organizers** (manage events) and **Customers** (browse events and book tickets) — with JWT-based authentication, role-based access control, and asynchronous background tasks.

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 3.2.5 (Java 17) |
| Build Tool | Gradle |
| Database | H2 (in-memory) |
| ORM | Spring Data JPA (Hibernate) |
| Authentication | Spring Security 6 + JWT (jjwt) |
| Background Tasks | Spring `@Async` + `ApplicationEventPublisher` |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |

## Design Decisions

### 1. H2 In-Memory Database
Chose H2 for zero-configuration setup — no external database installation required. The schema is auto-generated from JPA entities via `ddl-auto: create-drop`. To switch to PostgreSQL, update `application.yml` datasource properties and add the `postgresql` driver dependency.

### 2. Interface-Based Controller & Service Pattern
Controllers and services follow an interface-implementation pattern (`IEventController` → `EventControllerImpl`, `IEventService` → `EventServiceImpl`). API annotations (`@RestController`, `@RequestMapping`, `@GetMapping`, etc.) live on the interface, while `@Component` implementation classes handle the logic. This cleanly separates the API contract from business logic and improves testability.

### 3. Centralized Query Constants & Error Codes
- **`Queries.java`**: All native SQL queries in one place — avoids scattering SQL across repositories
- **`ErrorCode.java`**: Enum with structured codes (`E001`, `E100`, etc.) and descriptions — every exception references a specific error code
- **`TableName.java`**: Entity table name constants, referenced in `@Table` annotations

### 4. Optimistic Locking for Ticket Booking
The `Event` entity uses a `@Version` field for optimistic locking. When a customer books tickets, the service decrements `availableTickets` within a `@Transactional` method. If two concurrent bookings try to modify the same event, Hibernate detects the version mismatch and throws `OptimisticLockingFailureException`, returned as a 409 Conflict response.

### 5. Spring Application Events + @Async for Background Tasks
Uses Spring's `ApplicationEventPublisher` with `@EventListener` and `@Async` instead of an external message broker:
- **Decoupled architecture**: Services publish events; listeners handle notifications independently
- **Asynchronous processing**: `@Async("notificationExecutor")` runs notifications on a separate thread pool
- **Zero external dependencies**: No Redis, RabbitMQ, or Kafka needed

Two background tasks:
- **Booking Confirmation**: Triggered on successful booking — logs a simulated confirmation email
- **Event Update Notification**: Triggered on event update — notifies all customers who booked tickets

### 6. JWT Stateless Authentication
JSON Web Tokens for stateless auth. Token payload contains `userId`, `email`, and `role`. Spring Security's filter chain validates the token on every request. No server-side session storage.

### 7. Role-Based Access Control + Ownership Enforcement
- **Role-based**: Spring Security restricts endpoints by role (ORGANIZER or CUSTOMER)
- **Ownership**: Service layer verifies organizers can only modify their own events, customers can only view their own bookings

### 8. Structured Error Responses
A `@RestControllerAdvice` global exception handler returns consistent error responses with `id`, `type`, `code`, and `message` fields. Handles validation errors, not-found, authorization failures, optimistic locking conflicts, and unexpected errors.

### 9. API Versioning
All endpoints use the `/v1/` prefix (`/v1/auth`, `/v1/events`, `/v1/bookings`) for future-proof API versioning.

## Getting Started

### Prerequisites
- Java 17+

### Run the Application

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The API will start at `http://localhost:8080`.

### Access Points
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:eventdb`, username: `sa`, no password)

## API Endpoints

### Authentication (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/v1/auth/register` | Register a new user |
| POST | `/v1/auth/login` | Login and receive JWT token |

### Events

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/v1/events` | No | Any | Browse all events (paginated, filterable) |
| GET | `/v1/events/{id}` | No | Any | Get event details |
| POST | `/v1/events` | Yes | ORGANIZER | Create a new event |
| PATCH | `/v1/events/{id}` | Yes | ORGANIZER (owner) | Update an event |
| DELETE | `/v1/events/{id}` | Yes | ORGANIZER (owner) | Delete an event |

### Bookings

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/v1/bookings` | Yes | CUSTOMER | Book tickets |
| GET | `/v1/bookings` | Yes | CUSTOMER | List my bookings |
| GET | `/v1/bookings/{id}` | Yes | CUSTOMER (owner) | Get booking details |

## Sample API Usage

### Register an Organizer
```bash
curl -X POST http://localhost:8080/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Organizer",
    "email": "alice@example.com",
    "password": "password123",
    "role": "ORGANIZER"
  }'
```

### Register a Customer
```bash
curl -X POST http://localhost:8080/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bob Customer",
    "email": "bob@example.com",
    "password": "password123",
    "role": "CUSTOMER"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123"
  }'
```

### Create an Event (Organizer)
```bash
curl -X POST http://localhost:8080/v1/events \
  -H "Authorization: Bearer <organizer-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spring Music Festival",
    "description": "A grand music festival featuring top artists",
    "dateTime": "2026-06-15T18:00:00",
    "venue": "Central Park Arena",
    "totalTickets": 500,
    "price": 49.99
  }'
```

### Browse Events (No Auth Required)
```bash
curl http://localhost:8080/v1/events
curl "http://localhost:8080/v1/events?search=music&page=0&size=10"
```

### Book Tickets (Customer)
```bash
curl -X POST http://localhost:8080/v1/bookings \
  -H "Authorization: Bearer <customer-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": 1,
    "quantity": 3
  }'
# Console will show: [EMAIL] Booking confirmation sent to bob@example.com...
```

### Update Event (Organizer) — Triggers Notifications
```bash
curl -X PATCH http://localhost:8080/v1/events/1 \
  -H "Authorization: Bearer <organizer-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spring Music Festival - Updated!",
    "description": "Updated venue and time",
    "dateTime": "2026-06-20T19:00:00",
    "venue": "Downtown Concert Hall",
    "totalTickets": 500,
    "price": 59.99
  }'
# Console will show: [EMAIL] Notification sent to bob@example.com: Event updated...
```

## Project Structure

```
com.cactro.eventbooking/
├── config/              # SecurityConfig, JwtAuthFilter, JwtUtil, AsyncConfig
├── constant/            # ErrorCode, ExceptionType, Queries, TableName
├── controller/          # Interface controllers (IAuthController, IEventController, IBookingController)
│   └── impl/            # Controller implementations
├── dto/
│   ├── request/         # Request DTOs with validation
│   └── response/        # Response DTOs (Response wrapper, Error, AuthResponse, etc.)
├── entity/              # JPA entities (User, Role enum, Event, Booking)
├── event/               # Spring application events & listeners
├── exception/           # GlobalExceptionHandler, custom exceptions
├── repository/          # Spring Data JPA repositories
├── service/             # Service interfaces
│   └── impl/            # Service implementations (@Slf4j, @Transactional)
└── EventBookingApplication.java
```

## Background Tasks Demo

When you book tickets, the console will display:
```
========================================
[BACKGROUND TASK - BOOKING CONFIRMATION]
========================================
[EMAIL] Sending booking confirmation to Bob Customer (bob@example.com)
[EMAIL] You have successfully booked 3 ticket(s) for "Spring Music Festival".
[EMAIL] Total amount: $149.97
[EMAIL] Confirmation email sent successfully!
========================================
```

When an organizer updates an event with existing bookings:
```
==========================================
[BACKGROUND TASK - EVENT UPDATE NOTIFICATION]
==========================================
[NOTIFICATION] Event "Spring Music Festival" (ID: 1) has been updated.
[NOTIFICATION] Notifying 2 booked customer(s)...
[EMAIL] Notification sent to bob@example.com: Event "Spring Music Festival" has been updated.
[EMAIL] Notification sent to carol@example.com: Event "Spring Music Festival" has been updated.
[NOTIFICATION] All customers notified successfully!
==========================================
```
