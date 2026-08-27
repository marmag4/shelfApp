package gr.aueb.shelfapp.dto;

/** What we send back after a successful login: the token, plus a few basics. */
public record LoginResponse(
        String token,
        Long userId,
        String email,
        String firstName,
        String lastName
) {
}
