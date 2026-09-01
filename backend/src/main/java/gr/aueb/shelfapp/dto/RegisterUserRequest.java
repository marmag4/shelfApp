package gr.aueb.shelfapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** What the client sends to register a new user.
 */
public record RegisterUserRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        LocalDate birthDate,
        String city,
        String street,
        String streetNumber,
        String postalCode
) {
}
