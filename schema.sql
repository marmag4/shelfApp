-- ShelfApp database schema
-- Run once against the shelfapp database to create all tables.

-- Categories: a small shared lookup list (Dairy, Fruits, Vegetables, ...)
CREATE TABLE categories (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Users: one account per person using the app
CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    birth_date    DATE,
    city          VARCHAR(100),
    street        VARCHAR(150),
    street_number VARCHAR(20),
    postal_code   VARCHAR(20),
    created_at    TIMESTAMP NOT NULL DEFAULT now()
);

-- Products: what a user has in their fridge / pantry
CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    quantity    NUMERIC(10,2) NOT NULL,
    unit        VARCHAR(20) NOT NULL,               -- e.g. kg, g, l, ml, pcs
    expiry_date DATE NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                CHECK (status IN ('ACTIVE','CONSUMED','DONATED','WASTED')),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

-- SharingPoints: places that accept food donations
CREATE TABLE sharing_points (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    city          VARCHAR(100) NOT NULL,
    street        VARCHAR(150),
    street_number VARCHAR(20),
    postal_code   VARCHAR(20),
    phone         VARCHAR(30)
);

-- Donations: a product given away to a sharing point
CREATE TABLE donations (
    id               BIGSERIAL PRIMARY KEY,
    product_id       BIGINT NOT NULL REFERENCES products(id),
    sharing_point_id BIGINT NOT NULL REFERENCES sharing_points(id),
    donation_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                     CHECK (status IN ('PENDING','COMPLETED','CANCELLED'))
);

-- WasteLog: a product that ended up thrown away
CREATE TABLE waste_logs (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    waste_date DATE NOT NULL DEFAULT CURRENT_DATE,
    reason     VARCHAR(30) NOT NULL
               CHECK (reason IN ('EXPIRED','SPOILED','OVERBOUGHT','OTHER'))
);
