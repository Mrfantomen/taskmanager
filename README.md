# Task Manager API

A RESTful task management API built with Spring Boot, created as a learning project to explore backend development with Java, Spring, and JPA.

This is an evolving project: it currently covers task CRUD, user accounts with a one-to-many relationship, authentication with Spring Security, and richer task logic (filtering, sorting, priority, categories, validation). It will be extended with centralized error handling and a frontend as I progress through my studies.

## Features

- Create, read, update and delete tasks (full CRUD)
- Each task has a title, description, completion status, deadline, priority and categories
- User accounts, where each task belongs to a user (one-to-many relationship)
- User registration with Argon2id password hashing (OWASP-recommended)
- Authentication and authorization with Spring Security (users only see their own tasks)
- Server-side task ownership (the API ignores any user supplied in the request body and uses the authenticated user)
- Password hashes are never exposed in API responses
- **Priority** as a typed enum (`LOW`, `MEDIUM`, `HIGH`) — invalid values are rejected with `400 Bad Request`
- **Categories** as their own entity, owned per user (`@ManyToMany` relationship between tasks and categories)
- **Filtering** tasks via query parameters: `?completed=`, `?dueBefore=`, `?priority=`
- **Sorting** tasks via `?sortBy=`, validated against an allow-list (reusable `SortValidator` class)
- **Business rules**: titles are required, cannot be only whitespace, max 200 chars; deadlines cannot be in the past
- Category names are normalized on save (lowercased, trimmed) so `"Work"` and `"  WORK  "` are the same category
- Proper HTTP status codes (e.g. `201 Created` on registration, `400 Bad Request` for invalid input, `409 Conflict` for duplicate category names, `404 Not Found` for missing tasks, `204 No Content` on delete)
- Seed data on startup (dev profile only) so users `johan` and `anna` exist without manual registration
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
├── config/                  # SecurityConfig, DataSeeder
├── model/                   # Data models (Task, TaskUser, Category, Priority enum)
├── dto/                     # Data transfer objects (RegisterRequest, TaskRequest)
├── repository/              # Database access (Spring Data JPA)
├── service/                 # Business logic, authentication, validation
├── controller/              # REST endpoints (HTTP layer)
└── validation/              # Reusable validators (SortValidator)
```

A request flows from the **controller** (receives the HTTP call) to the **service** (business logic and validation) to the **repository** (database access), keeping each layer focused on a single responsibility. Relationships: `Task` references its owner `TaskUser` via `@ManyToOne`; `Task` and `Category` are joined via `@ManyToMany` (with a `task_category` join table); `Category` references its owner `TaskUser` via `@ManyToOne`. Spring Security sits in front of the controller layer and rejects unauthenticated requests before they reach application code.

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
| GET    | `/tasks`        | Get all tasks belonging to the authenticated user. Supports query parameters: `completed`, `dueBefore` (ISO date), `priority` (`LOW`/`MEDIUM`/`HIGH`), `sortBy` (`id`/`title`/`deadline`/`completed`) |
| GET    | `/tasks/{id}`   | Get a single task by id                           |
| POST   | `/tasks`        | Create a new task (owned by the authenticated user). Body fields: `title`, `description`, `completed`, `deadline`, `priority`, `categoryIds` |
| PUT    | `/tasks/{id}`   | Update an existing task (same body fields as POST)|
| DELETE | `/tasks/{id}`   | Delete a task                                     |

### Categories

All category endpoints require authentication. Categories are private per user.

| Method | Endpoint            | Description                              |
| ------ | ------------------- | ---------------------------------------- |
| GET    | `/categories`       | List the authenticated user's categories |
| GET    | `/categories/{id}`  | Get a single category by id              |
| POST   | `/categories`       | Create a new category (body: `name`, `color`). Names are normalized to lowercase and trimmed |
| DELETE | `/categories/{id}`  | Delete a category                        |

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

> The `dev` profile also seeds `johan` and `anna` automatically, so this step is optional locally.

Create a couple of categories:

```bash
curl -u johan:hemligt123 -X POST http://localhost:8080/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Work","color":"#FF5733"}'

curl -u johan:hemligt123 -X POST http://localhost:8080/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Personal","color":"#33C7FF"}'
```

Create a task with priority and categories:

```bash
curl -u johan:hemligt123 -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Prepare meeting","completed":false,"deadline":"2026-12-15","priority":"HIGH","categoryIds":[1,2]}'
```

Filter tasks:

```bash
# Only completed tasks
curl -u johan:hemligt123 "http://localhost:8080/tasks?completed=true"

# Only HIGH priority
curl -u johan:hemligt123 "http://localhost:8080/tasks?priority=HIGH"

# Only tasks with a deadline before a certain date
curl -u johan:hemligt123 "http://localhost:8080/tasks?dueBefore=2026-12-31"
```

Sort tasks:

```bash
curl -u johan:hemligt123 "http://localhost:8080/tasks?sortBy=deadline"
```

Update a task:

```bash
curl -u johan:hemligt123 -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Prepare meeting","description":"Slides ready","completed":true,"deadline":"2026-12-15","priority":"MEDIUM","categoryIds":[1]}'
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
- [x] Filtering, sorting and priorities for tasks
- [x] Categories as their own entity with a many-to-many relationship to tasks
- [x] Business rules for task validation (required title, max length, no past deadlines)
- [ ] Combining multiple filters in one request (e.g. `?completed=false&priority=HIGH`) — currently the first matching filter wins; a proper solution needs a dynamic query via `Specification` or similar
- [ ] Re-enable CSRF protection when a web frontend is added (currently disabled because the API is consumed via curl only)
- [ ] Proper login/logout endpoints (currently using HTTP Basic Authentication)
- [ ] Ownership checks on update/delete so users cannot modify tasks they do not own
- [ ] Centralized error handling with `@ControllerAdvice` and Bean Validation annotations (so that the current validation in `TaskService.validateTask` can move to a more declarative style)
- [ ] Decide how categories behave on shared tasks (likely visible-but-read-only, or per-user tagging)
- [ ] Migrate from H2 to PostgreSQL
- [ ] API documentation with Swagger
- [ ] A frontend interface
- [ ] (Future) Sharing tasks with other users (read/write permissions)

## About

A learning project for backend development with Java and Spring Boot.
