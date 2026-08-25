package gr.aueb.shelfapp.entity;

/** Matches the CHECK constraint on waste_logs.reason in schema.sql. */
public enum WasteReason {
    EXPIRED,
    SPOILED,
    OVERBOUGHT,
    OTHER
}
