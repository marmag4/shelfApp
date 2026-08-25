package gr.aueb.shelfapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** e.g. {"productId": 1, "reason": "EXPIRED"} */
public record RecordWasteRequest(
        @NotNull Long productId,
        @NotBlank String reason
) {
}
