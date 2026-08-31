package gr.aueb.shelfapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gr.aueb.shelfapp.dto.CreateDonationRequest;
import gr.aueb.shelfapp.entity.Donation;
import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import gr.aueb.shelfapp.entity.SharingPoint;
import gr.aueb.shelfapp.entity.User;
import gr.aueb.shelfapp.repository.DonationRepository;
import gr.aueb.shelfapp.repository.ProductRepository;
import gr.aueb.shelfapp.repository.SharingPointRepository;
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
 * Same idea as WasteLogServiceTest: donating a product means two things
 * happen together - a Donation row is created AND the product's status
 * flips to DONATED. Everything is mocked (no real database), so these
 * check only the logic in DonationService itself.
 */
@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    private static final Long OWNER_ID = 5L;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SharingPointRepository sharingPointRepository;

    @Mock
    private Product product;

    @Mock
    private User owner;

    @Mock
    private SharingPoint sharingPoint;

    @InjectMocks
    private DonationService donationService;

    @BeforeEach
    void setUp() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(product.getUser()).thenReturn(owner);
        when(owner.getId()).thenReturn(OWNER_ID);
    }

    @Test
    void create_flipsProductStatusToDonated_andSavesBoth() {
        when(sharingPointRepository.findById(2L)).thenReturn(Optional.of(sharingPoint));
        when(product.getId()).thenReturn(10L);
        when(product.getName()).thenReturn("Milk");
        when(sharingPoint.getId()).thenReturn(2L);
        when(sharingPoint.getName()).thenReturn("Local food bank");
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateDonationRequest request = new CreateDonationRequest(1L, 2L);

        donationService.create(request, OWNER_ID);

        verify(product).setStatus(ProductStatus.DONATED);
        verify(productRepository).save(product);
        verify(donationRepository).save(any(Donation.class));
    }

    @Test
    void create_throwsForbidden_whenProductBelongsToSomeoneElse() {
        CreateDonationRequest request = new CreateDonationRequest(1L, 2L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> donationService.create(request, 999L));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        // Nothing should have been saved, and we shouldn't even look up the
        // sharing point - the ownership check must happen first.
        verify(donationRepository, never()).save(any());
        verify(productRepository, never()).save(any());
        verify(sharingPointRepository, never()).findById(any());
    }

    @Test
    void create_throwsNotFound_whenSharingPointDoesNotExist() {
        when(sharingPointRepository.findById(2L)).thenReturn(Optional.empty());

        CreateDonationRequest request = new CreateDonationRequest(1L, 2L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> donationService.create(request, OWNER_ID));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(donationRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }
}
