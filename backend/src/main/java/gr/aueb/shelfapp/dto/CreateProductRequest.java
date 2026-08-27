package gr.aueb.shelfapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * What the client sends us to add a product to the pantry.
 * Note: no userId here - the product always belongs to whoever is logged
 * in (read from the JWT in the controller), never to a user id chosen by
 * the client.
 */
public record CreateProductRequest(
        @NotBlank String name,
        @NotNull @DecimalMin(value = "0.01", message = "Quantity must be greater than zero") BigDecimal quantity,
        @NotBlank String unit,
        @NotNull @FutureOrPresent(message = "Expiry date can't be in the past") LocalDate expiryDate,
        @NotNull Long categoryId
) {
}
