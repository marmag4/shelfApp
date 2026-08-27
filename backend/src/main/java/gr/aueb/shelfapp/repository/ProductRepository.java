package gr.aueb.shelfapp.repository;

import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByUserId(Long userId);

    /** Used for the expiry-notifications feature: still-active products expiring soon (or already overdue). */
    List<Product> findByUserIdAndStatusAndExpiryDateLessThanEqual(
            Long userId, ProductStatus status, LocalDate maxExpiryDate);
}
