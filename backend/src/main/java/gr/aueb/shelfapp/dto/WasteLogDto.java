package gr.aueb.shelfapp.dto;

import java.time.LocalDate;

public record WasteLogDto(
        Long id,
        Long productId,
        String productName,
        LocalDate wasteDate,
        String reason
) {
}
