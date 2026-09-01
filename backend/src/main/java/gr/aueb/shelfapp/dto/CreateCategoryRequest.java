package gr.aueb.shelfapp.dto;

import jakarta.validation.constraints.NotBlank;

/** What the client sends to create a new category.
 */
public record CreateCategoryRequest(@NotBlank String name) {
}
