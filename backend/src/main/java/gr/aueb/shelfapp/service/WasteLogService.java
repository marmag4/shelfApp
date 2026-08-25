package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.dto.RecordWasteRequest;
import gr.aueb.shelfapp.dto.WasteLogDto;
import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import gr.aueb.shelfapp.entity.WasteLog;
import gr.aueb.shelfapp.entity.WasteReason;
import gr.aueb.shelfapp.repository.ProductRepository;
import gr.aueb.shelfapp.repository.WasteLogRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * The one place that knows "wasting a product" means two things happening
 * together: a WasteLog row gets created, AND the product's status flips to
 * WASTED. Keeping both here (in one @Transactional method) means they can
 * never drift out of sync with each other.
 */
@Service
public class WasteLogService {

    private final WasteLogRepository wasteLogRepository;
    private final ProductRepository productRepository;

    public WasteLogService(WasteLogRepository wasteLogRepository, ProductRepository productRepository) {
        this.wasteLogRepository = wasteLogRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public WasteLogDto recordWaste(RecordWasteRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        WasteReason reason;
        try {
            reason = WasteReason.valueOf(request.reason().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "reason must be one of EXPIRED, SPOILED, OVERBOUGHT, OTHER");
        }

        WasteLog saved = wasteLogRepository.save(new WasteLog(product, reason));

        product.setStatus(ProductStatus.WASTED);
        productRepository.save(product);

        return toDto(saved);
    }

    public List<WasteLogDto> findByUser(Long userId) {
        return wasteLogRepository.findByProduct_User_Id(userId).stream()
                .map(this::toDto)
                .toList();
    }

    private WasteLogDto toDto(WasteLog log) {
        return new WasteLogDto(
                log.getId(),
                log.getProduct().getId(),
                log.getProduct().getName(),
                log.getWasteDate(),
                log.getReason().name()
        );
    }
}
