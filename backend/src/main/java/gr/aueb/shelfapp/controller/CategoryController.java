package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.CategoryDto;
import gr.aueb.shelfapp.dto.CreateCategoryRequest;
import gr.aueb.shelfapp.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Controller layer: only handles HTTP in/out.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getAll() {
        return categoryService.findAll();
    }

    @PostMapping
    public CategoryDto create(@Valid @RequestBody CreateCategoryRequest request) {
        return categoryService.create(request.name());
    }
}
