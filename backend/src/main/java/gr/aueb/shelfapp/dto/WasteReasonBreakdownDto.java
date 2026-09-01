package gr.aueb.shelfapp.dto;

/**
 * How many of the logged-in user's waste logs fall under a given reason in
 * chart on the Statistics page. All 4 reasons always come back (zero for
 * any the user has never logged).
 */
public record WasteReasonBreakdownDto(
        String reason,
        long count
) {
}
