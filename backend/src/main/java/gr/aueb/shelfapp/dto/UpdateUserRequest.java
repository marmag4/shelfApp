package gr.aueb.shelfapp.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * What the client sends to edit their own profile.
 * Deliberately does NOT include email or password.
 */
public record UpdateUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        LocalDate birthDate,
        String city,
        String street,
        String streetNumber,
        String postalCode
) {
}
