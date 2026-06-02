# Task Manager API

A RESTful task management API built with Spring Boot, created as a learning project to explore backend development with Java, Spring, and JPA.

This is an evolving project: it currently covers task CRUD, user accounts with a one-to-many relationship, and authentication with Spring Security. It will be extended with input validation, richer task logic, and a frontend as I progress through my studies.

## Features

- Create, read, update and delete tasks (full CRUD)
- Each task has a title, description, completion status and deadline
- User accounts, where each task belongs to a user (one-to-many relationship)
- User registration with Argon2id password hashing (OWASP-recommended)
- Authentication and authorization with Spring Security (users only see their own tasks)
- Server-side task ownership (the API ignores any user supplied in the request body and uses the authenticated user)
- Password hashes are never exposed in API responses
- Custom query methods for filtering tasks (by completion status, deadline, and owner)
- Proper HTTP status codes (e.g. `201 Created` on registration, `404 Not Found` for missing tasks, `204 No Content` on delete)
- In-memory H2 database for fast, zero-setup development

## Tech stack

- **Java 17**
- **Spring Boot 3.5.14** (Spring Web, Spring Data JPA, Spring Security)
- **Argon2id** password hashing via **Bouncy Castle**
- **H2** in-memory database
- **Maven** for build and dependency management

## Architecture

The project follows a standard layered architecture:

```
com.example.taskmanager
├── TaskmanagerApplication   # Application entry point
├── config/                  # SecurityConfig (Spring Security, password encoder)
├── model/                   # Data models (Task, TaskUser)
├── dto/                     # Data transfer objects (RegisterRequest)
├── repository/              # Database access (Spring Data JPA)
├── service/                 # Business logic and authentication
└── controller/              # REST endpoints (HTTP layer)
```

A request flows from the **controller** (receives the HTTP call) to the **service** (business logic) to the **repository** (database access), keeping each layer focused on a single responsibility. A `Task` references the `TaskUser` it belongs to via a `@ManyToOne` relationship, stored as a foreign key. Spring Security sits in front of the controller layer and rejects unauthenticated requests before they reach application code.

## API endpoints

### Auth

| Method | Endpoint           | Description                          |
| ------ | ------------------ | ------------------------------------ |
| POST   | `/auth/register`   | Register a new user (public)         |

All other endpoints require authentication. HTTP Basic Authentication is currently used for development.

### Tasks

All task endpoints require authentication. Requests are scoped to the authenticated user.

| Method | Endpoint        | Description                                       |
| ------ | --------------- | ------------------------------------------------- |
| GET    | `/tasks`        | Get all tasks belonging to the authenticated user |
| GET    | `/tasks/{id}`   | Get a single task by id                           |
| POST   | `/tasks`        | Create a new task (owned by the authenticated user) |
| PUT    | `/tasks/{id}`   | Update an existing task                           |
| DELETE | `/tasks/{id}`   | Delete a task                                     |

### Users

| Method | Endpoint               | Description                       |
| ------ | ---------------------- | --------------------------------- |
| GET    | `/users`               | Get all users                     |
| GET    | `/users/{id}`          | Get a single user by id           |
| POST   | `/users`               | Create a new user                 |
| PUT    | `/users/{id}`          | Update an existing user           |
| DELETE | `/users/{id}`          | Delete a user                     |
| GET    | `/users/{id}/tasks`    | Get all tasks for a specific user |

## Running the application

### Requirements

- Java 17 or higher
- Maven (or use the included Maven wrapper)

### Start the app

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

> **Note:** the H2 database runs in-memory, so all data is reset every time the application restarts. This is intentional for development.

## Example usage

Register a user:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"johan","password":"hemligt123"}'
```

Create a task as the authenticated user (the task is automatically owned by them):

```bash
curl -u johan:hemligt123 -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn Spring Security","completed":false,"deadline":"2026-06-01"}'
```

Get the authenticated user's tasks:

```bash
curl -u johan:hemligt123 http://localhost:8080/tasks
```

Update a task:

```bash
curl -u johan:hemligt123 -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn Spring Security","description":"Updated","completed":true,"deadline":"2026-06-15"}'
```

Delete a task:

```bash
curl -u johan:hemligt123 -X DELETE http://localhost:8080/tasks/1
```

## Screenshots

### Application startup

The app boots with Spring Boot's embedded Tomcat server and connects to an in-memory H2 database.

![Application startup log](screenshots/startup.png)

### Full CRUD cycle via curl

Creating, reading, updating and deleting tasks through the API, ending with an empty list after deletion.

![Full CRUD cycle in the terminal](screenshots/crudTerminal.png)

### Retrieving all tasks in the browser

A GET request to `/tasks` returns all stored tasks as JSON.

![List of tasks returned as JSON](screenshots/threeTasks.png)

## Roadmap

Planned improvements as the project grows:

- [x] User accounts with a one-to-many relationship to tasks
- [x] Authentication and authorization with Spring Security (users only see their own tasks)
- [ ] Re-enable CSRF protection when a web frontend is added (currently disabled because the API is consumed via curl only)
- [ ] Proper login/logout endpoints (currently using HTTP Basic Authentication)
- [ ] Ownership checks on update/delete so users cannot modify tasks they do not own
- [ ] Input validation and centralized error handling (`@ControllerAdvice`)
- [ ] Filtering, sorting and priorities for tasks
- [ ] Migrate from H2 to PostgreSQL
- [ ] API documentation with Swagger
- [ ] A frontend interface
- [ ] (Future) Sharing tasks with other users (read/write permissions)

## About

A learning project for backend development with Java and Spring Boot.
