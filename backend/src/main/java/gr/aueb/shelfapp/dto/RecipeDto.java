package gr.aueb.shelfapp.dto;

import java.util.List;

/** A simple recipe suggestion for using up a product before it gets wasted. */
public record RecipeDto(
        String title,
        String description,
        List<String> ingredients,
        String instructions
) {
}
