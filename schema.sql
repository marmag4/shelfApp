-- ShelfApp database schema
-- Categories
CREATE TABLE categories (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO categories (name) VALUES
    ('Dairy'),
    ('Fruits'),
    ('Vegetables'),
    ('Meat'),
    ('Fish & Seafood'),
    ('Eggs'),
    ('Bakery'),
    ('Grains & Pasta'),
    ('Canned Goods'),
    ('Frozen Foods'),
    ('Beverages'),
    ('Condiments & Sauces'),
    ('Snacks'),
    ('Herbs & Spices'),
    ('Deli & Cold Cuts'),
    ('Leftovers')
ON CONFLICT (name) DO NOTHING;

-- Users
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

-- Products
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

-- SharingPoints
CREATE TABLE sharing_points (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    city          VARCHAR(100) NOT NULL,
    street        VARCHAR(150),
    street_number VARCHAR(20),
    postal_code   VARCHAR(20),
    phone         VARCHAR(30)
);

-- Donations
CREATE TABLE donations (
    id               BIGSERIAL PRIMARY KEY,
    product_id       BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sharing_point_id BIGINT NOT NULL REFERENCES sharing_points(id),
    donation_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                     CHECK (status IN ('PENDING','COMPLETED','CANCELLED'))
);

-- WasteLog
CREATE TABLE waste_logs (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    waste_date DATE NOT NULL DEFAULT CURRENT_DATE,
    reason     VARCHAR(30) NOT NULL
               CHECK (reason IN ('EXPIRED','SPOILED','OVERBOUGHT','OTHER'))
);
