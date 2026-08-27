-- ShelfApp database schema
-- Run once against the shelfapp database to create all tables.

-- Categories: a small shared lookup list (Dairy, Fruits, Vegetables, ...)
CREATE TABLE categories (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Seeded with a broad, generic set covering what a household fridge/pantry
-- usually holds - so a brand new install doesn't start with an empty
-- dropdown. Users can still add their own categories any time from the
-- "+ Add category" button on the Pantry screen.
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
-- ON DELETE CASCADE on product_id: deleting a user cascades to their
-- products (see products.user_id below), which must in turn cascade here,
-- otherwise Postgres would refuse to delete a product that still has a
-- donation record pointing at it.
CREATE TABLE donations (
    id               BIGSERIAL PRIMARY KEY,
    product_id       BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sharing_point_id BIGINT NOT NULL REFERENCES sharing_points(id),
    donation_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                     CHECK (status IN ('PENDING','COMPLETED','CANCELLED'))
);

-- WasteLog: a product that ended up thrown away
-- Same reasoning as donations.product_id above.
CREATE TABLE waste_logs (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    waste_date DATE NOT NULL DEFAULT CURRENT_DATE,
    reason     VARCHAR(30) NOT NULL
               CHECK (reason IN ('EXPIRED','SPOILED','OVERBOUGHT','OTHER'))
);
