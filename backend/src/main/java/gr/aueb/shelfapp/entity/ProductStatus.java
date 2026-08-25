package gr.aueb.shelfapp.entity;

/** Matches the CHECK constraint on products.status in schema.sql. */
public enum ProductStatus {
    ACTIVE,
    CONSUMED,
    DONATED,
    WASTED
}
