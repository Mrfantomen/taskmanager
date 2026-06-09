# Task Manager API

A RESTful task management API built with Spring Boot, created as a learning project to explore backend development with Java, Spring, and JPA.

This is an evolving project: it currently covers task CRUD, user accounts, authentication with Spring Security, richer task logic (filtering, sorting, priority, categories, validation), security hardening, production-readiness features (response DTOs, centralized error handling, PostgreSQL, Swagger), and a role-based admin system. It will be extended with a frontend as the project grows.

## Features

- Create, read, update and delete tasks (full CRUD)
- Each task has a title, description, completion status, deadline, priority and categories
- User accounts, where each task belongs to a user (one-to-many relationship)
- User registration with Argon2id password hashing (OWASP-recommended)
- Authentication and authorization with Spring Security (users only see their own tasks — list, get, update and delete)
- Object-level authorization (BOLA/IDOR protection): tasks are always scoped to the authenticated user
- Server-side task ownership (the API ignores any user supplied in the request body and uses the authenticated user)
- Password hashes are never exposed in API responses (dedicated response DTOs for all entities)
- Centralized error handling via `@ControllerAdvice` with consistent JSON error responses
- **Priority** as a typed enum (`LOW`, `MEDIUM`, `HIGH`) — invalid values are rejected with `400 Bad Request`
- **Categories** as their own entity, owned per user (`@ManyToMany` relationship between tasks and categories)
- **Filtering** tasks via query parameters — supports combining multiple filters: `?completed=`, `?dueBefore=`, `?priority=`, combinable via Spring Data `Specification`
- **Sorting** tasks via `?sortBy=`, validated against an allow-list (reusable `SortValidator` class)
- **Business rules**: titles are required, cannot be only whitespace, max 200 chars; deadlines cannot be in the past
- Category names are normalized on save (lowercased, trimmed) so `"Work"` and `"  WORK  "` are the same category
- Proper HTTP status codes (e.g. `201 Created` on registration, `400 Bad Request` for invalid input, `409 Conflict` for duplicate category names, `404 Not Found` for missing resources, `204 No Content` on delete)
- Seed data on startup (dev profile only) so users `johan` and `anna` exist with categories and tasks without manual setup
- **PostgreSQL** support via Docker for persistent storage (prod profile)
- **Swagger / OpenAPI** documentation available at `/swagger-ui.html`
- **Role-based access control**: users have a `USER` or `ADMIN` role — admins can access `/admin/**` endpoints
- **Admin endpoints** under `/admin/**` protected by both request-level (`SecurityConfig`) and method-level (`@PreAuthorize`) security — defence in depth
- Seed data includes an `admin` user (dev profile only) with the `ADMIN` role

## Tech stack

- **Java 17**
- **Spring Boot 3.5.14** (Spring Web, Spring Data JPA, Spring Security)
- **Argon2id** password hashing via **Bouncy Castle**
- **H2** in-memory database (dev profile)
- **PostgreSQL 16** via Docker (prod profile)
- **Springdoc OpenAPI** for Swagger UI
- **Maven** for build and dependency management

## Architecture

The project follows a standard layered architecture:

```
com.example.taskmanager
├── TaskmanagerApplication   # Application entry point
├── config/                  # SecurityConfig, DataSeeder, GlobalExceptionHandler, OpenApiConfig
├── model/                   # Data models (Task, TaskUser, Category, Priority enum, Role enum)
├── dto/                     # Data transfer objects (RegisterRequest, TaskRequest, TaskResponse,
│                            #   TaskUserResponse, CategoryResponse, ErrorResponse)
├── repository/              # Database access (Spring Data JPA + Specification support)
├── service/                 # Business logic, authentication, validation, DTO mapping
├── controller/              # REST endpoints (TaskController, CategoryController,
│                            #   TaskUserController, AuthController, AdminController)
└── validation/              # Reusable validators (SortValidator, TaskSpecification)
```

A request flows from the **controller** (receives the HTTP call) to the **service** (business logic, validation and DTO mapping) to the **repository** (database access), keeping each layer focused on a single responsibility. Controllers return response DTOs — never raw JPA entities. Relationships: `Task` references its owner `TaskUser` via `@ManyToOne`; `Task` and `Category` are joined via `@ManyToMany` (with a `task_category` join table); `Category` references its owner `TaskUser` via `@ManyToOne`. Spring Security sits in front of the controller layer and rejects unauthenticated requests before they reach application code.

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
| GET    | `/tasks`        | Get all tasks belonging to the authenticated user. Supports query parameters: `completed`, `dueBefore` (ISO date), `priority` (`LOW`/`MEDIUM`/`HIGH`), `sortBy` (`id`/`title`/`deadline`/`completed`). Multiple filters can be combined. |
| GET    | `/tasks/{id}`   | Get a single task by id (scoped to authenticated user) |
| POST   | `/tasks`        | Create a new task (owned by the authenticated user). Body fields: `title`, `description`, `completed`, `deadline`, `priority`, `categoryIds` |
| PUT    | `/tasks/{id}`   | Update an existing task (same body fields as POST) |
| DELETE | `/tasks/{id}`   | Delete a task (scoped to authenticated user)       |

### Categories

All category endpoints require authentication. Categories are private per user.

| Method | Endpoint            | Description                              |
| ------ | ------------------- | ---------------------------------------- |
| GET    | `/categories`       | List the authenticated user's categories |
| GET    | `/categories/{id}`  | Get a single category by id              |
| POST   | `/categories`       | Create a new category (body: `name`, `color`). Names are normalized to lowercase and trimmed |
| DELETE | `/categories/{id}`  | Delete a category                        |

### Users

| Method | Endpoint               | Description                                    |
| ------ | ---------------------- | ---------------------------------------------- |
| POST   | `/users`               | Create a new user                              |
| PUT    | `/users/{id}`          | Update an existing user                        |
| DELETE | `/users/{id}`          | Delete a user                                  |

### Admin

All admin endpoints require the `ADMIN` role. Protected by both `SecurityConfig` and `@PreAuthorize`.

| Method | Endpoint                    | Description                              |
| ------ | --------------------------- | ---------------------------------------- |
| GET    | `/admin/users`              | List all users in the system             |
| GET    | `/admin/users/{id}`         | Get a specific user by id                |
| GET    | `/admin/users/{id}/tasks`   | Get all tasks for a specific user        |
| GET    | `/admin/tasks`              | List all tasks in the system             |
| GET    | `/admin/tasks/{id}`         | Get any task by id                       |

## Running the application

### Requirements

- Java 17 or higher
- Maven (or use the included Maven wrapper)

### Start the app (dev profile — H2 in-memory database)

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`. The dev profile seeds three users automatically:

| Username | Password     | Role    |
| -------- | ------------ | ------- |
| `johan`  | `hemligt123` | `USER`  |
| `anna`   | `annapass123`| `USER`  |
| `admin`  | `admin123`   | `ADMIN` |

Johan gets a welcome task with categories and HIGH priority. Anna gets a medium priority task.

### Start the app (prod profile — PostgreSQL)

First start the PostgreSQL container:

```bash
sudo docker start taskmanager-db
```

If the container does not exist yet, create it:

```bash
sudo docker run --name taskmanager-db \
  -e POSTGRES_DB=taskmanager \
  -e POSTGRES_USER=taskuser \
  -e POSTGRES_PASSWORD=taskpass \
  -p 5432:5432 \
  -d postgres:16
```

Then start the app with the prod profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Data persists across restarts when using PostgreSQL.

### API documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

## Example usage

Register a user:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"johan","password":"hemligt123"}'
```

> The `dev` profile seeds `johan` and `anna` automatically, so this step is optional locally.

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

Filter and combine filters:

```bash
# Only HIGH priority tasks that are not completed
curl -u johan:hemligt123 "http://localhost:8080/tasks?priority=HIGH&completed=false"

# Tasks with deadline before a certain date, sorted by title
curl -u johan:hemligt123 "http://localhost:8080/tasks?dueBefore=2026-12-31&sortBy=title"
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

The project is built in steps, each adding a focused layer of functionality. Completed steps are checked off; remaining steps describe what is planned and roughly in what order.

### ✅ Step 1 — Core CRUD API

- [x] Task entity with title, description, completion status and deadline
- [x] Full CRUD endpoints for tasks
- [x] In-memory H2 database, layered architecture (controller → service → repository)

### ✅ Step 2 — Users and ownership

- [x] `TaskUser` entity
- [x] One-to-many relationship: each task belongs to a user
- [x] Endpoint to fetch all tasks for a specific user

### ✅ Step 3 — Authentication and authorization

- [x] Spring Security configured with `SecurityConfig`
- [x] User registration with Argon2id password hashing (OWASP-recommended)
- [x] Users only see their own tasks — on list, get, update and delete (object-level authorization fixed in Step 5)
- [x] Server-side task ownership on `POST` and `PUT` (the request body cannot override the owner)
- [x] Password hashes hidden from API responses

### ✅ Step 4 — Richer task logic

- [x] Filtering on `completed`, `dueBefore`, `priority`
- [x] Sorting via `?sortBy=`, with an allow-list validated by a reusable `SortValidator`
- [x] `Priority` enum (`LOW`, `MEDIUM`, `HIGH`) with automatic validation
- [x] `Category` as its own entity, private per user, with a many-to-many relationship to tasks
- [x] Business rules for task validation (required title, max length, no past deadlines)
- [x] Seed data on startup (dev profile only)

### ✅ Step 5 — Security hardening

- [x] **Object-level authorization (BOLA/IDOR fix)**: `GET /tasks/{id}`, `PUT /tasks/{id}` and `DELETE /tasks/{id}` are now scoped to the authenticated user via `findByIdAndUserUserid` and return `404` if the task is not yours.
- [x] **Safer task updates**: the existing task is now loaded and mutated rather than replaced with a new object built from the request body.
- [x] **Safer task deletion**: task ownership is verified before deletion.
- [x] **Remove or guard the `/users` endpoints**: `GET /users`, `GET /users/{id}` and `GET /users/{id}/tasks` now return `403 Forbidden` with a TODO comment until a proper admin role exists (Step 7).
- [x] **Document the chosen Argon2 parameters** in `SecurityConfig` with a comment explaining the OWASP recommendation.

### ✅ Step 6 — Polish and production-readiness

- [x] **Response DTOs**: controllers return dedicated response DTOs (`TaskResponse`, `TaskUserResponse`, `CategoryResponse`, `ErrorResponse`) — never raw JPA entities.
- [x] **Centralized error handling** via `GlobalExceptionHandler` (`@RestControllerAdvice`) with consistent JSON error responses including status, message and timestamp.
- [x] **Combining multiple filters** in one request via Spring Data `Specification` — e.g. `?completed=false&priority=HIGH` works correctly.
- [x] **Remove unused repository methods** (`findByCompleted`, `findByDeadline`, `findAll` without user scope).
- [x] **Replaced `System.out.println` with SLF4J logging**.
- [x] **Migrated from H2 to PostgreSQL** via Docker (prod profile) for persistent storage.
- [x] **API documentation with Swagger / OpenAPI** available at `/swagger-ui.html`.

### ✅ Step 7 — Admin role

- [x] Added `Role` enum (`USER`, `ADMIN`) and `role` field on `TaskUser` (defaults to `USER`).
- [x] `CustomUserDetailsService` maps the role to a Spring Security authority (`ROLE_USER`, `ROLE_ADMIN`).
- [x] `SecurityConfig` protects `/admin/**` with `hasRole("ADMIN")` at request level.
- [x] `AdminController` under `/admin/**` with endpoints for listing users, inspecting any user's tasks, and listing all tasks.
- [x] Admin-only methods in `TaskService` annotated with `@PreAuthorize("hasRole('ADMIN')")` — defence in depth.
- [x] `DataSeeder` bootstraps an `admin` user with the `ADMIN` role in the dev profile.
- [x] Object-level authorization (BOLA/IDOR) remains enforced for user-specific operations via `findByIdAndUserUserid` — role checks and object-level checks are complementary layers.

### 🌐 Step 8 — Frontend

- [ ] Build a frontend interface (technology to be decided later).
- [ ] Proper login/logout endpoints (currently using HTTP Basic Authentication).
- [ ] Re-enable CSRF protection (currently disabled because the API is consumed via curl only).

### 🔐 Step 9 — Sharing and advanced authentication

- [ ] Sharing tasks with other users (read/write permissions).
- [ ] Decide how categories behave on shared tasks (likely visible-but-read-only, or per-user tagging).
- [ ] Multi-factor authentication (TOTP first, with an extensible design that can support additional factors like YubiKey/WebAuthn and GPG-based challenges).

## About

A learning project for backend development with Java and Spring Boot.
