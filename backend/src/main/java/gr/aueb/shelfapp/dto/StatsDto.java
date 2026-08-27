package gr.aueb.shelfapp.dto;

/** A summary of how the logged-in user is doing overall. */
public record StatsDto(
        long totalProducts,
        long activeProducts,
        long consumedProducts,
        long donatedProducts,
        long wastedProducts,
        long totalDonations,
        long totalWasteLogs,
        double wastePercentage
) {
}
