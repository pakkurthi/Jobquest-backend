# Job Portal Backend - Developer Guide

## Architecture Overview

The Job Portal Backend is a Spring Boot 3.3.0 application with a clear layered architecture:

- **Controller Layer**: REST endpoints for job seekers, job providers, and authentication
- **Service Layer**: Business logic and transaction management
- **Repository Layer**: Data access using Spring Data JPA
- **Security**: JWT-based authentication with role-based access control

### Key Components

- **Security**: JWT authentication via `AuthTokenFilter` and `JwtUtils`, with roles `JOB_SEEKER` and `JOB_PROVIDER`
- **Models**: Core domain entities like `Job`, `User`, with JPA annotations
- **DTOs**: Request/Response objects for data transfer (`CreateJobRequest`, `JobResponse`, etc.)
- **Controllers**: REST endpoints with role-based access control via `@PreAuthorize` annotations

## Development Workflow

### Building and Running

```bash
# Run the application
mvn spring-boot:run

# Build the application
mvn clean package

# Run tests
mvn test
```

### Authentication Flow

1. User registers via `/api/auth/signup` with a `role` of either `JOB_SEEKER` or `JOB_PROVIDER`
2. User logs in via `/api/auth/signin` to receive a JWT token
3. Token is included in subsequent requests via the `Authorization: Bearer <token>` header

### Security Patterns

- Method-level security is enabled with `@EnableMethodSecurity(prePostEnabled = true)`
- Controllers use `@PreAuthorize("hasRole('ROLE_NAME')")` for role-based access control
- When using `hasRole('JOB_PROVIDER')`, Spring Security automatically prefixes with `ROLE_`

## Common Patterns and Conventions

### Error Handling

Controller methods wrap service calls in try-catch blocks and throw `RuntimeException` with messages:

```java
try {
    JobResponse jobResponse = jobService.createJob(request);
    return ResponseEntity.ok(jobResponse);
} catch (RuntimeException e) {
    throw new RuntimeException(e.getMessage());
}
```

### Authentication

Current user access pattern in services:

```java
User currentUser = authService.getCurrentUser();
// Check if user has correct role
if (currentUser.getRole() != Role.JOB_PROVIDER) {
    throw new RuntimeException("Only Job Providers can create job postings");
}
```

### Debugging

- Debugging is set up with `System.out.println` statements in critical components like `AuthTokenFilter`
- JWT validation messages are logged in the console

## Database Configuration

- PostgreSQL database with connection details in `application.properties`
- JPA settings: `spring.jpa.hibernate.ddl-auto=update` (schema auto-update)

## Known Issues and Workarounds

- Some controllers may have issues with role-based access when `@PreAuthorize` annotations are used
- As a temporary fix, some endpoints are marked with `permitAll()` in `WebSecurityConfig`
- When encountering 401 Unauthorized errors despite valid JWT, check both method-level security and HTTP security configuration
