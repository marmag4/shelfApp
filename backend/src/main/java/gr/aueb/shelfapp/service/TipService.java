package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.dto.TipDto;
import gr.aueb.shelfapp.entity.Category;
import gr.aueb.shelfapp.repository.CategoryRepository;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Service;

/**
 * Feature #3 from the project idea: a short motivational message about why
 * a category of food matters, so using it up (instead of wasting it) feels
 * like a small win rather than a chore.
 *
 * Unlike Product/Donation/WasteLog etc, these tips are curated app content,
 * not something a user creates or that needs its own database table - so
 * they're simply seeded here in code, matched by category name.
 */
@Service
public class TipService {

    private static final Map<String, List<String>> TIPS_BY_CATEGORY = Map.of(
            "Dairy", List.of(
                    "Milk and yogurt are packed with calcium and protein - great for bones and muscles!",
                    "A little cheese goes a long way: it's calorie-dense, so using it up is easy on your wallet too."
            ),
            "Fruits", List.of(
                    "Fruit is full of vitamins and fiber - eating it before it turns keeps both you and your budget happy.",
                    "Overripe fruit is perfect for a smoothie - nothing needs to go to waste!"
            ),
            "Vegetables", List.of(
                    "Vegetables are some of the most nutrient-dense food there is - don't let them sit forgotten in the fridge.",
                    "A wilting vegetable is still great in a soup or stir-fry - texture matters less once it's cooked."
            ),
            "Bakery", List.of(
                    "Stale bread makes excellent croutons or breadcrumbs - it doesn't have to be thrown away.",
                    "Bread freezes really well - if you won't finish it in time, freeze it today instead of wasting it later."
            ),
            "Meat", List.of(
                    "Meat has a real environmental and financial cost - using it before it spoils really matters.",
                    "Not sure you'll cook it in time? Freezing it early is an easy way to stop the clock on spoilage."
            )
    );

    private static final List<String> GENERAL_TIPS = List.of(
            "Food waste is one of the easiest ways to waste money without noticing it - every saved product counts!",
            "Around a third of all food produced worldwide is wasted - using up what you already have really helps.",
            "Planning meals around what's about to expire is one of the simplest habits that actually saves money."
    );

    private final CategoryRepository categoryRepository;
    private final Random random = new Random();

    public TipService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /** categoryId is optional - null (or an unrecognized category) falls back to a general tip. */
    public TipDto randomTip(Long categoryId) {
        String categoryName = null;
        List<String> pool = GENERAL_TIPS;

        if (categoryId != null) {
            categoryName = categoryRepository.findById(categoryId)
                    .map(Category::getName)
                    .orElse(null);

            List<String> categoryTips = categoryName != null ? TIPS_BY_CATEGORY.get(categoryName) : null;
            if (categoryTips != null && !categoryTips.isEmpty()) {
                pool = categoryTips;
            }
        }

        String message = pool.get(random.nextInt(pool.size()));
        return new TipDto(categoryName, message);
    }
}
