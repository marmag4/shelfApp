package gr.aueb.shelfapp.entity;

/** Matches the CHECK constraint on donations.status in schema.sql. */
public enum DonationStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}
