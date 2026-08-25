package gr.aueb.shelfapp.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSharingPointRequest(
        @NotBlank String name,
        @NotBlank String city,
        String street,
        String streetNumber,
        String postalCode,
        String phone
) {
}
