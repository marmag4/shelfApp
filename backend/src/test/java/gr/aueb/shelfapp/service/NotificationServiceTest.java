package gr.aueb.shelfapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import gr.aueb.shelfapp.dto.NotificationDto;
import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import gr.aueb.shelfapp.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the "how urgent is this?" logic in NotificationService - the rule
 * behind feature #1 (warn before food expires). Uses real Product objects
 * (just built with `new`, never saved) instead of mocks, since all we need
 * from them here is a real expiry date for the date-math to work on.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Product productExpiringIn(long days) {
        return new Product("Milk", BigDecimal.ONE, "pcs", LocalDate.now().plusDays(days), null, null);
    }

    @Test
    void flagsAnAlreadyPastExpiryDateAsOverdue() {
        when(productRepository.findByUserIdAndStatusAndExpiryDateLessThanEqual(
                anyLong(), any(ProductStatus.class), any(LocalDate.class)))
                .thenReturn(List.of(productExpiringIn(-2)));

        List<NotificationDto> result = notificationService.findForUser(1L);

        assertEquals("OVERDUE", result.get(0).urgency());
        assertEquals(-2, result.get(0).daysUntilExpiry());
    }

    @Test
    void flagsTodayAndTomorrowAsUrgent() {
        when(productRepository.findByUserIdAndStatusAndExpiryDateLessThanEqual(
                anyLong(), any(ProductStatus.class), any(LocalDate.class)))
                .thenReturn(List.of(productExpiringIn(0), productExpiringIn(1)));

        List<NotificationDto> result = notificationService.findForUser(1L);

        assertEquals("URGENT", result.get(0).urgency());
        assertEquals("URGENT", result.get(1).urgency());
    }

    @Test
    void flagsAnythingFurtherOutAsWarning() {
        when(productRepository.findByUserIdAndStatusAndExpiryDateLessThanEqual(
                anyLong(), any(ProductStatus.class), any(LocalDate.class)))
                .thenReturn(List.of(productExpiringIn(3)));

        List<NotificationDto> result = notificationService.findForUser(1L);

        assertEquals("WARNING", result.get(0).urgency());
        assertEquals(3, result.get(0).daysUntilExpiry());
    }

    @Test
    void sortsResultsSoTheMostUrgentComesFirst() {
        // Deliberately out of order - the service must sort them itself.
        when(productRepository.findByUserIdAndStatusAndExpiryDateLessThanEqual(
                anyLong(), any(ProductStatus.class), any(LocalDate.class)))
                .thenReturn(List.of(productExpiringIn(3), productExpiringIn(-1), productExpiringIn(0)));

        List<NotificationDto> result = notificationService.findForUser(1L);

        assertEquals(-1, result.get(0).daysUntilExpiry());
        assertEquals(0, result.get(1).daysUntilExpiry());
        assertEquals(3, result.get(2).daysUntilExpiry());
    }
}
