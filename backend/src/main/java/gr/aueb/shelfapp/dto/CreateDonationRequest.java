package gr.aueb.shelfapp.dto;

import jakarta.validation.constraints.NotNull;

public record CreateDonationRequest(
        @NotNull Long productId,
        @NotNull Long sharingPointId
) {
}
