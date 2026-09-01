package gr.aueb.shelfapp.dto;

import java.time.LocalDate;

/**
 * One "This is expiring" item.
 * DaysUntilExpiry can be negative - that means it already expired and is
 * still sitting there marked ACTIVE (nobody logged it as wasted/donated).
 * Need to fix it later.
 */
public record NotificationDto(
        Long productId,
        String productName,
        LocalDate expiryDate,
        long daysUntilExpiry,
        String urgency
) {
}
