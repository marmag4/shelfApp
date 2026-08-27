package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.dto.StatsDto;
import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import gr.aueb.shelfapp.repository.DonationRepository;
import gr.aueb.shelfapp.repository.ProductRepository;
import gr.aueb.shelfapp.repository.WasteLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Feature #5 from the project idea: a simple summary of how the logged-in
 * user is doing overall - how many products stayed active, got eaten, got
 * donated, or ended up wasted.
 *
 * "wastePercentage" is the headline number: of all the products that are no
 * longer active (eaten, donated, or wasted), what share ended up wasted?
 * That's the number that actually reflects whether the user is improving.
 */
@Service
public class StatsService {

    private final ProductRepository productRepository;
    private final DonationRepository donationRepository;
    private final WasteLogRepository wasteLogRepository;

    public StatsService(ProductRepository productRepository,
                         DonationRepository donationRepository,
                         WasteLogRepository wasteLogRepository) {
        this.productRepository = productRepository;
        this.donationRepository = donationRepository;
        this.wasteLogRepository = wasteLogRepository;
    }

    public StatsDto getStats(Long userId) {
        List<Product> products = productRepository.findByUserId(userId);

        long active = countByStatus(products, ProductStatus.ACTIVE);
        long consumed = countByStatus(products, ProductStatus.CONSUMED);
        long donated = countByStatus(products, ProductStatus.DONATED);
        long wasted = countByStatus(products, ProductStatus.WASTED);

        long resolved = consumed + donated + wasted;
        double wastePercentage = resolved > 0 ? (wasted * 100.0 / resolved) : 0.0;
        // round to 1 decimal place, e.g. 33.3 instead of 33.33333333333333
        wastePercentage = Math.round(wastePercentage * 10.0) / 10.0;

        long totalDonations = donationRepository.findByProduct_User_Id(userId).size();
        long totalWasteLogs = wasteLogRepository.findByProduct_User_Id(userId).size();

        return new StatsDto(
                products.size(),
                active,
                consumed,
                donated,
                wasted,
                totalDonations,
                totalWasteLogs,
                wastePercentage
        );
    }

    private long countByStatus(List<Product> products, ProductStatus status) {
        return products.stream().filter(p -> p.getStatus() == status).count();
    }
}
