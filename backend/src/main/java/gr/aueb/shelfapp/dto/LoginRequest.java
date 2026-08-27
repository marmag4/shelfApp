package gr.aueb.shelfapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** What the client sends us to log in. */
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
