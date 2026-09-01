package gr.aueb.shelfapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gr.aueb.shelfapp.dto.RecordWasteRequest;
import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import gr.aueb.shelfapp.entity.User;
import gr.aueb.shelfapp.entity.WasteLog;
import gr.aueb.shelfapp.repository.ProductRepository;
import gr.aueb.shelfapp.repository.WasteLogRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Tests for the logic in the whole app where "two things must
 * happen together".
 */
@ExtendWith(MockitoExtension.class)
class WasteLogServiceTest {

    private static final Long OWNER_ID = 5L;

    @Mock
    private WasteLogRepository wasteLogRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Product product;

    @Mock
    private User owner;

    @InjectMocks
    private WasteLogService wasteLogService;

    @BeforeEach
    void setUp() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(product.getUser()).thenReturn(owner);
        when(owner.getId()).thenReturn(OWNER_ID);
    }

    @Test
    void recordWaste_flipsProductStatusToWasted_andSavesBoth() {
        when(product.getId()).thenReturn(10L);
        when(product.getName()).thenReturn("Milk");
        when(wasteLogRepository.save(any(WasteLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RecordWasteRequest request = new RecordWasteRequest(1L, "EXPIRED");

        wasteLogService.recordWaste(request, OWNER_ID);

        verify(product).setStatus(ProductStatus.WASTED);
        verify(productRepository).save(product);
        verify(wasteLogRepository).save(any(WasteLog.class));
    }

    @Test
    void recordWaste_throwsForbidden_whenProductBelongsToSomeoneElse() {
        RecordWasteRequest request = new RecordWasteRequest(1L, "EXPIRED");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> wasteLogService.recordWaste(request, 999L));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        // Nothing should have been saved - the ownership check must happen before any writes.
        verify(wasteLogRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void recordWaste_throwsBadRequest_forAnInvalidReason() {
        RecordWasteRequest request = new RecordWasteRequest(1L, "NOT_A_REAL_REASON");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> wasteLogService.recordWaste(request, OWNER_ID));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(wasteLogRepository, never()).save(any());
    }
}
