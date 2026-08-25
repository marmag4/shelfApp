package gr.aueb.shelfapp.dto;

public record SharingPointDto(
        Long id,
        String name,
        String city,
        String street,
        String streetNumber,
        String postalCode,
        String phone
) {
}
