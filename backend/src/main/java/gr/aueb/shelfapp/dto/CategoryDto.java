package gr.aueb.shelfapp.dto;

/** What we send back to the client. Never expose the @Entity directly. */
public record CategoryDto(Long id, String name) {
}
