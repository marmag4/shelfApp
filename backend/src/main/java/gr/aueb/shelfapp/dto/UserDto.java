package gr.aueb.shelfapp.dto;

import java.time.LocalDate;

/** What we send back to the client.
 */
public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String city,
        String street,
        String streetNumber,
        String postalCode
) {
}
