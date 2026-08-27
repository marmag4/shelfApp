package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.dto.RecipeDto;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Feature #2 from the project idea: recipe suggestions for a product that's
 * about to expire, so it gets used instead of wasted.
 *
 * Like TipService, these are curated app content rather than something
 * fetched from an external site - reliable to demo without depending on
 * internet access or a third-party API being up on the day of your
 * presentation.
 */
@Service
public class RecipeService {

    private static final Map<String, List<RecipeDto>> RECIPES_BY_CATEGORY = Map.of(
            "Dairy", List.of(
                    new RecipeDto(
                            "Quick Yogurt Parfait",
                            "A simple layered snack that uses up yogurt before it turns.",
                            List.of("yogurt", "honey or jam", "any leftover fruit", "a handful of oats or granola"),
                            "Layer yogurt, fruit, and oats in a glass. Drizzle with honey. Ready in 5 minutes."
                    ),
                    new RecipeDto(
                            "Cheese & Milk Bechamel Sauce",
                            "Turns milk and leftover cheese into a base for pasta bakes or lasagna.",
                            List.of("milk", "cheese", "butter", "flour"),
                            "Melt butter, whisk in flour, slowly add milk while stirring until it thickens, then stir in the cheese until melted."
                    )
            ),
            "Fruits", List.of(
                    new RecipeDto(
                            "Overripe Fruit Smoothie",
                            "The classic fix for fruit that's gone a bit too soft to eat as-is.",
                            List.of("any soft/overripe fruit", "milk or yogurt", "a spoon of honey (optional)"),
                            "Blend everything together until smooth. Add ice if you want it colder."
                    ),
                    new RecipeDto(
                            "Simple Stewed Fruit",
                            "Softens fruit further and makes it last a few more days as a topping.",
                            List.of("fruit, chopped", "a splash of water", "a spoon of sugar (optional)"),
                            "Simmer chopped fruit with a splash of water for 5-10 minutes until soft. Great on yogurt, oats, or toast."
                    )
            ),
            "Vegetables", List.of(
                    new RecipeDto(
                            "Anything-Goes Stir-Fry",
                            "Uses up vegetables that are starting to wilt, no matter which ones you have.",
                            List.of("any vegetables you have", "garlic", "oil", "soy sauce"),
                            "Chop the vegetables, fry garlic in oil, add the vegetables and stir-fry for 5-7 minutes, season with soy sauce."
                    ),
                    new RecipeDto(
                            "Leftover Vegetable Soup",
                            "A forgiving recipe - almost any vegetable works here.",
                            List.of("any vegetables you have", "stock or water", "an onion", "salt and pepper"),
                            "Saute the onion, add chopped vegetables and stock, simmer for 20 minutes, then blend if you like it smooth."
                    )
            ),
            "Bakery", List.of(
                    new RecipeDto(
                            "Simple Bread Croutons",
                            "Turns stale bread into something crunchy and useful instead of throwing it away.",
                            List.of("stale bread", "olive oil", "salt", "any dried herbs (optional)"),
                            "Cube the bread, toss with olive oil and salt, bake at 180C for 10-12 minutes until golden."
                    ),
                    new RecipeDto(
                            "Quick French Toast",
                            "A classic way to use bread that's a bit too dry to eat plain.",
                            List.of("bread", "egg", "milk", "a pinch of sugar or cinnamon"),
                            "Whisk the egg with milk and sugar, dip the bread slices, fry in a pan until golden on both sides."
                    )
            ),
            "Meat", List.of(
                    new RecipeDto(
                            "One-Pan Meat & Vegetables",
                            "A straightforward way to cook meat before it needs to be thrown out.",
                            List.of("meat", "any vegetables you have", "oil", "salt and pepper"),
                            "Season the meat, sear it in a hot pan, add chopped vegetables, and cook everything together until done."
                    )
            )
    );

    /** Unknown/unmatched category -> empty list, rather than an error. */
    public List<RecipeDto> forCategory(String categoryName) {
        return RECIPES_BY_CATEGORY.getOrDefault(categoryName, List.of());
    }
}
