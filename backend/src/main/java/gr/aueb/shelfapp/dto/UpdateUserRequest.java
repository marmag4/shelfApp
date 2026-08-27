package gr.aueb.shelfapp.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * What the client sends to edit their own profile. Deliberately does NOT
 * include email or password - changing those needs its own, more careful
 * flow (email is the login identity; password changes should re-check the
 * current one), so this only covers the "personal details" half of User.
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
