package gr.aueb.shelfapp.dto;

import java.time.LocalDate;

public record DonationDto(
        Long id,
        Long productId,
        String productName,
        Long sharingPointId,
        String sharingPointName,
        LocalDate donationDate,
        String status
) {
}
