# ShelfApp

A household pantry tracker built for the Coding Factory 10 (AUEB) final project.

You log the food you bring into your kitchen — name, quantity, expiry date, category — and
ShelfApp helps you avoid wasting it:

1. **Notifications** — warns you before a product goes bad (color-coded by urgency).
2. **Recipe suggestions** — a curated recipe idea for a product's category, so it gets used
   instead of thrown away.
3. **Motivational tips** — a short message about why a category of food matters.
4. **Donations** — a place to give a product away (a "sharing point") instead of wasting it.
5. **Statistics** — how your own habits are trending: counts by status and a waste percentage.

## Tech stack

| Layer     | Technology |
|-----------|------------|
| Backend   | Java 21, Spring Boot 3, Spring Data JPA, Spring Security, JJWT (JWT), springdoc-openapi (Swagger) |
| Database  | PostgreSQL 16 |
| Frontend  | React (Vite), React Router, Axios |
| Auth      | JSON Web Tokens (JWT), stateless, checked on both backend and frontend |
| Container | Docker, docker-compose |

The backend follows a layered architecture: **Controller → Service → Repository**, exposed as
a REST API and documented with Swagger/OpenAPI. The frontend is a React SPA that talks to that
REST API over HTTP.

## Domain model

- **Category** — a reference list (Dairy, Fruits, Vegetables, Bakery, Meat, ...).
- **User** — one account per person, with a hashed password (BCrypt).
- **Product** — an item in a user's pantry: name, quantity, unit, expiry date, status
  (`ACTIVE` / `CONSUMED` / `DONATED` / `WASTED`), belongs to one Category and one User.
- **WasteLog** — created when a product is marked wasted; records the date and reason
  (`EXPIRED` / `SPOILED` / `OVERBOUGHT` / `OTHER`).
- **SharingPoint** — a place that accepts food donations (a food bank, charity, etc.).
- **Donation** — created when a product is donated to a SharingPoint; has its own status
  (`PENDING` / `COMPLETED` / `CANCELLED`).

The full table definitions are in [`schema.sql`](./schema.sql).

## Project structure

```
FinalProject/
├── backend/            Spring Boot REST API
├── frontend/            React SPA (Vite)
├── schema.sql            Database schema (run automatically by Docker on first start)
└── docker-compose.yml    Runs db + backend + frontend together
```

## Running the whole app with Docker (recommended)

This is the easiest way to run ShelfApp — one command starts the database, backend, and
frontend together, with the database schema created automatically.

**Requirements:** Docker Desktop (or Docker Engine + Docker Compose) installed and running.

```bash
git clone https://github.com/marmag4/shelfApp.git
cd shelfApp
docker compose up --build
```

The first run will take a few minutes (it downloads base images and builds the backend and
frontend). Once you see `Started ShelfAppApplication` in the logs, the app is ready:

- **Frontend:** http://localhost:5173
- **Backend API:** http://localhost:8080/api
- **Swagger UI (API docs):** http://localhost:8080/swagger-ui/index.html

Open the frontend, click **Register**, create an account, then log in — the pantry starts
empty for a new account.

To stop everything:

```bash
docker compose down
```

(Your data is kept in a Docker volume and survives `docker compose down` — only
`docker compose down -v` would delete it.)

## Running it for local development (without Docker for backend/frontend)

Useful if you want to edit the code and see changes instantly, instead of rebuilding
containers each time.

**Requirements:** Java 21, Maven, Node.js 20+, and Docker (for the database only).

1. **Start only the database:**

   ```bash
   docker compose up -d db
   ```

2. **Run the backend** (from the `backend` folder):

   ```bash
   cd backend
   mvn spring-boot:run
   ```

   Or open the project in IntelliJ and run `ShelfAppApplication`. It starts on
   http://localhost:8080.

3. **Run the frontend** (from the `frontend` folder, in a separate terminal):

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

   It starts on http://localhost:5173 with hot-reload — code changes appear immediately.

## Authentication

- `POST /api/auth/login` — logs in with email + password, returns a JWT.
- Every other endpoint (except registration, login, and Swagger itself) requires a valid
  `Authorization: Bearer <token>` header — enforced by a Spring Security filter chain.
- The current user is always read from the JWT, never trusted from client input — so one
  user can never view or modify another user's products (returns `403 Forbidden`).
- The frontend stores the token in `localStorage`, attaches it to every request automatically
  (see `frontend/src/api/client.js`), and uses a `ProtectedRoute` wrapper so pages that
  require login redirect to `/login` if the user isn't authenticated.

## API documentation

Once the backend is running, the full REST API is documented (and testable) via Swagger UI:

http://localhost:8080/swagger-ui/index.html

Click **Authorize**, paste a JWT obtained from `POST /api/auth/login`, and you can try out
every endpoint directly from the browser.

## Main API endpoints

| Method | Path                          | Description                              |
|--------|-------------------------------|-------------------------------------------|
| POST   | `/api/users`                  | Register a new user                       |
| POST   | `/api/auth/login`             | Log in, get a JWT                         |
| GET    | `/api/users/me`                | Current user's profile                    |
| GET/POST | `/api/categories`            | List / create categories                  |
| GET/POST | `/api/products`              | List / add my products                    |
| GET    | `/api/products/{id}`          | One of my products                        |
| PATCH  | `/api/products/{id}/status`   | Change a product's status                 |
| GET    | `/api/products/{id}/recipes`  | Recipe suggestions for that product       |
| GET/POST | `/api/waste-logs`            | List / record a waste log                 |
| GET/POST | `/api/sharing-points`        | List / add donation points                |
| GET/POST | `/api/donations`             | List / record a donation                  |
| GET    | `/api/notifications`          | Products expiring soon                    |
| GET    | `/api/tips/random`            | A random motivational tip                 |
| GET    | `/api/stats`                  | My usage statistics                       |

## Author

Maria — Coding Factory 10, AUEB.
