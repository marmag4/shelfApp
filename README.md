# ShelfApp

A household pantry tracker — Coding Factory 10 (AUEB) final project.

Log the food in your kitchen and ShelfApp helps you avoid wasting it:

- Warns you before a product expires
- Suggests a recipe so it gets used
- Shows a motivational tip
- Lets you donate a product instead of wasting it
- Tracks your habits over time, as numbers and charts

You can also edit a product's details, manage your account (edit or delete it), and add your own
pantry categories any time.

## Tech stack

- Backend: Java, Spring Boot, Spring Data JPA, Spring Security (JWT), Swagger
- Database: PostgreSQL
- Frontend: React, React Router, Axios
- Container: Docker, docker-compose

## Domain model

- Category — Dairy, Fruits, Vegetables
- User — one account per person, password hashed
- Product — name, quantity, unit, expiry date, status; belongs to a category and a user
- WasteLog — created when a product is wasted
- SharingPoint — a place that accepts donations
- Donation — created when a product is donated

Full schema: `schema.sql`.

## Run it with Docker (recommended)

Requires Docker Desktop running.

1. Clone the repo, `cd` into it
2. `docker compose up --build`
3. Wait for "Started ShelfAppApplication"

Then open:

- Frontend: http://localhost:5173
- Swagger: http://localhost:8080/swagger-ui/index.html

Register an account and log in. To stop: `docker compose down`.

## Run it for local development

Requires Java 21, Maven, Node.js 20+, and Docker (for the database).

1. `docker compose up -d db`
2. Backend: `cd backend` then `mvn spring-boot:run` (or run `ShelfAppApplication` in IntelliJ)
3. Frontend, in a new terminal: `cd frontend`, `npm install`, `npm run dev`

## Authentication

JWT-based. Log in via `POST /api/auth/login` to get a token. Every other endpoint (except
register/login/Swagger) needs an `Authorization: Bearer <token>` header. The current user is
always read from the token, never from client input.

## Testing

Unit tests cover the service layer's key logic (mocked, no database needed):

- `WasteLogServiceTest`
- `DonationServiceTest`
- `NotificationServiceTest`

Run with `mvn test` from `backend`, or from IntelliJ.

