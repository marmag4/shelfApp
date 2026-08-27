package gr.aueb.shelfapp.dto;

/** A short motivational message, optionally tied to a category (e.g. "Dairy"). */
public record TipDto(
        String category,
        String message
) {
}
