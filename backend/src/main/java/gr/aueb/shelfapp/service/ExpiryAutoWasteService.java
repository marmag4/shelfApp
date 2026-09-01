package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import gr.aueb.shelfapp.entity.WasteLog;
import gr.aueb.shelfapp.entity.WasteReason;
import gr.aueb.shelfapp.repository.ProductRepository;
import gr.aueb.shelfapp.repository.WasteLogRepository;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sweeps every user's pantry for products that are still ACTIVE but whose
 * expiry date has already passed, and automatically wastes them - a
 * WasteLog with reason EXPIRED gets created and the product's status
 * flips to WASTED, exactly like the manual "Wasted" action in the UI
 * (WasteLogService), just without a human clicking the button.
 *
 * Runs once a day at 01:00 via the cron schedule, and once more right when
 * the app starts up. The startup run matters because this app isn't
 * expected to stay running 24/7 during development/demoing - without it, a
 * product that expired while the app was stopped would just sit there
 * ACTIVE until the next 01:00 tick, instead of being caught as soon as the
 * app comes back up.
 */
@Service
public class ExpiryAutoWasteService {

    private static final Logger log = LoggerFactory.getLogger(ExpiryAutoWasteService.class);

    private final ProductRepository productRepository;
    private final WasteLogRepository wasteLogRepository;

    public ExpiryAutoWasteService(ProductRepository productRepository, WasteLogRepository wasteLogRepository) {
        this.productRepository = productRepository;
        this.wasteLogRepository = wasteLogRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void wasteExpiredProducts() {
        List<Product> overdue = productRepository.findByStatusAndExpiryDateLessThan(
                ProductStatus.ACTIVE, LocalDate.now());

        if (overdue.isEmpty()) {
            return;
        }

        for (Product product : overdue) {
            wasteLogRepository.save(new WasteLog(product, WasteReason.EXPIRED));
            product.setStatus(ProductStatus.WASTED);
        }
        productRepository.saveAll(overdue);

        log.info("Auto-wasted {} product(s) past their expiry date", overdue.size());
    }
}
