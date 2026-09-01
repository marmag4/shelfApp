package gr.aueb.shelfapp.dto;

/**
 * One month's worth of activity for the Statistics page's trend chart.
 * It represends how many products got wasted vs donated that month.
 * Always 6 of these come back together, even for months with zero activity, so the chart has a consistent x-axis to draw.
 */
public record MonthlyTrendDto(
        String month,
        long wastedCount,
        long donatedCount
) {
}
