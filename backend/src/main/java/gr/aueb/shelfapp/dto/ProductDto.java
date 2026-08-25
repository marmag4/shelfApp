package gr.aueb.shelfapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductDto(
        Long id,
        String name,
        BigDecimal quantity,
        String unit,
        LocalDate expiryDate,
        String status,
        Long categoryId,
        String categoryName,
        Long userId
) {
}
