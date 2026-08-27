package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.dto.NotificationDto;
import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import gr.aueb.shelfapp.repository.ProductRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Builds the "your food is about to expire" list for the logged-in user -
 * feature #1 from the project idea: warn 3 days and 1 day before expiry.
 *
 * A product shows up here if it's still ACTIVE (not consumed/donated/wasted
 * yet) and its expiry date is within the next 3 days - or already passed,
 * so an ignored product doesn't just silently disappear from view.
 */
@Service
public class NotificationService {

    private static final int WARNING_WINDOW_DAYS = 3;

    private final ProductRepository productRepository;

    public NotificationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<NotificationDto> findForUser(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(WARNING_WINDOW_DAYS);

        List<Product> expiringSoon = productRepository
                .findByUserIdAndStatusAndExpiryDateLessThanEqual(userId, ProductStatus.ACTIVE, cutoff);

        return expiringSoon.stream()
                .map(p -> toDto(p, today))
                .sorted(Comparator.comparing(NotificationDto::daysUntilExpiry))
                .toList();
    }

    private NotificationDto toDto(Product product, LocalDate today) {
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, product.getExpiryDate());

        String urgency;
        if (daysUntilExpiry < 0) {
            urgency = "OVERDUE";
        } else if (daysUntilExpiry <= 1) {
            urgency = "URGENT";
        } else {
            urgency = "WARNING";
        }

        return new NotificationDto(
                product.getId(),
                product.getName(),
                product.getExpiryDate(),
                daysUntilExpiry,
                urgency
        );
    }
}
