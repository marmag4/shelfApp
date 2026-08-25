package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.dto.CreateDonationRequest;
import gr.aueb.shelfapp.dto.DonationDto;
import gr.aueb.shelfapp.entity.Donation;
import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import gr.aueb.shelfapp.entity.SharingPoint;
import gr.aueb.shelfapp.repository.DonationRepository;
import gr.aueb.shelfapp.repository.ProductRepository;
import gr.aueb.shelfapp.repository.SharingPointRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Same idea as WasteLogService: donating a product means two things happen
 * together - a Donation row is created (status PENDING), and the product's
 * status flips to DONATED, so it leaves the active pantry.
 */
@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final ProductRepository productRepository;
    private final SharingPointRepository sharingPointRepository;

    public DonationService(DonationRepository donationRepository,
                            ProductRepository productRepository,
                            SharingPointRepository sharingPointRepository) {
        this.donationRepository = donationRepository;
        this.productRepository = productRepository;
        this.sharingPointRepository = sharingPointRepository;
    }

    @Transactional
    public DonationDto create(CreateDonationRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        SharingPoint sharingPoint = sharingPointRepository.findById(request.sharingPointId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sharing point not found"));

        Donation saved = donationRepository.save(new Donation(product, sharingPoint));

        product.setStatus(ProductStatus.DONATED);
        productRepository.save(product);

        return toDto(saved);
    }

    public List<DonationDto> findByUser(Long userId) {
        return donationRepository.findByProduct_User_Id(userId).stream()
                .map(this::toDto)
                .toList();
    }

    private DonationDto toDto(Donation d) {
        return new DonationDto(
                d.getId(),
                d.getProduct().getId(),
                d.getProduct().getName(),
                d.getSharingPoint().getId(),
                d.getSharingPoint().getName(),
                d.getDonationDate(),
                d.getStatus().name()
        );
    }
}
