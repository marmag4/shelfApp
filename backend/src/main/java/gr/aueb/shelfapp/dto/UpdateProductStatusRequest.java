package gr.aueb.shelfapp.dto;

import jakarta.validation.constraints.NotBlank;

/** e.g. {"status": "CONSUMED"} - one of ACTIVE, CONSUMED, DONATED, WASTED. */
public record UpdateProductStatusRequest(@NotBlank String status) {
}
