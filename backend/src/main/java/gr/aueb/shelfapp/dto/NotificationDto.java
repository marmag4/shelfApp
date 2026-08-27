package gr.aueb.shelfapp.dto;

import java.time.LocalDate;

/**
 * One "heads up, this is expiring" item.
 * daysUntilExpiry can be negative - that means it already expired and is
 * still sitting there marked ACTIVE (nobody logged it as wasted/donated/etc).
 */
public record NotificationDto(
        Long productId,
        String productName,
        LocalDate expiryDate,
        long daysUntilExpiry,
        String urgency
) {
}
