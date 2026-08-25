package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.dto.CategoryDto;
import gr.aueb.shelfapp.entity.Category;
import gr.aueb.shelfapp.repository.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * The Service layer: the actual logic of "what does it mean to
 * list/create a category" lives here, not in the Controller.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .toList();
    }

    public CategoryDto create(String name) {
        Category saved = categoryRepository.save(new Category(name));
        return new CategoryDto(saved.getId(), saved.getName());
    }
}
