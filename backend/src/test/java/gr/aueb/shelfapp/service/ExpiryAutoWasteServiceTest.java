package gr.aueb.shelfapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import gr.aueb.shelfapp.entity.WasteLog;
import gr.aueb.shelfapp.repository.ProductRepository;
import gr.aueb.shelfapp.repository.WasteLogRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the startup sweep that auto-wastes products past their
 * expiry date (the fix for "a product stays ACTIVE forever after it
 * expires" - it now gets a WasteLog with reason EXPIRED and flips to
 * WASTED on its own, same as the manual "Wasted" button does).
 */
@ExtendWith(MockitoExtension.class)
class ExpiryAutoWasteServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WasteLogRepository wasteLogRepository;

    @InjectMocks
    private ExpiryAutoWasteService expiryAutoWasteService;

    private Product productExpiringIn(long days) {
        return new Product("Milk", BigDecimal.ONE, "pcs", LocalDate.now().plusDays(days), null, null);
    }

    @Test
    void wasteExpiredProducts_flipsOverdueActiveProductsToWasted_andLogsExpiredReason() {
        Product overdue = productExpiringIn(-3);
        when(productRepository.findByStatusAndExpiryDateLessThan(eq(ProductStatus.ACTIVE), any(LocalDate.class)))
                .thenReturn(List.of(overdue));

        expiryAutoWasteService.wasteExpiredProducts();

        assertEquals(ProductStatus.WASTED, overdue.getStatus());
        verify(wasteLogRepository).save(any(WasteLog.class));
        verify(productRepository).saveAll(List.of(overdue));
    }

    @Test
    void wasteExpiredProducts_doesNothing_whenNoProductsAreOverdue() {
        when(productRepository.findByStatusAndExpiryDateLessThan(eq(ProductStatus.ACTIVE), any(LocalDate.class)))
                .thenReturn(List.of());

        expiryAutoWasteService.wasteExpiredProducts();

        verify(wasteLogRepository, never()).save(any());
        verify(productRepository, never()).saveAll(any());
    }

    @Test
    void wasteExpiredProducts_handlesMultipleOverdueProductsInOneSweep() {
        Product first = productExpiringIn(-1);
        Product second = productExpiringIn(-10);
        when(productRepository.findByStatusAndExpiryDateLessThan(eq(ProductStatus.ACTIVE), any(LocalDate.class)))
                .thenReturn(List.of(first, second));

        expiryAutoWasteService.wasteExpiredProducts();

        assertEquals(ProductStatus.WASTED, first.getStatus());
        assertEquals(ProductStatus.WASTED, second.getStatus());
        verify(wasteLogRepository, times(2)).save(any(WasteLog.class));
    }

    @Test
    void wasteExpiredProducts_queriesForActiveProductsExpiredBeforeToday() {
        // Pins down the actual filter used, so a future refactor can't
        // accidentally loosen it to e.g. "<=" (which would also catch
        // products expiring today, before the day is even over).
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        when(productRepository.findByStatusAndExpiryDateLessThan(eq(ProductStatus.ACTIVE), dateCaptor.capture()))
                .thenReturn(List.of());

        expiryAutoWasteService.wasteExpiredProducts();

        assertEquals(LocalDate.now(), dateCaptor.getValue());
    }
}
