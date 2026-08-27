package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.CreateProductRequest;
import gr.aueb.shelfapp.dto.ProductDto;
import gr.aueb.shelfapp.dto.RecipeDto;
import gr.aueb.shelfapp.dto.UpdateProductStatusRequest;
import gr.aueb.shelfapp.security.CurrentUserProvider;
import gr.aueb.shelfapp.service.ProductService;
import gr.aueb.shelfapp.service.RecipeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Every endpoint here works on "my" products - the current user is read
 * from the JWT (via CurrentUserProvider), never trusted from the client.
 * Also: a product can only be viewed/changed by the user who owns it
 * (see ProductService.getOwnedOrThrow) - that's the "authorization" half,
 * on top of "authentication".
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final RecipeService recipeService;
    private final CurrentUserProvider currentUserProvider;

    public ProductController(ProductService productService, RecipeService recipeService,
                              CurrentUserProvider currentUserProvider) {
        this.productService = productService;
        this.recipeService = recipeService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request, currentUserProvider.getCurrentUserId());
    }

    /** Lists only the logged-in user's own products. */
    @GetMapping
    public List<ProductDto> getMine() {
        return productService.findByUser(currentUserProvider.getCurrentUserId());
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return productService.findById(id, currentUserProvider.getCurrentUserId());
    }

    /** Edits a product's own details (name/quantity/unit/expiry/category) - not its status. */
    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody CreateProductRequest request) {
        return productService.update(id, request, currentUserProvider.getCurrentUserId());
    }

    @PatchMapping("/{id}/status")
    public ProductDto updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateProductStatusRequest request) {
        return productService.updateStatus(id, request.status(), currentUserProvider.getCurrentUserId());
    }

    /** Feature #2: recipe ideas for this product's category, so it gets used before it expires. */
    @GetMapping("/{id}/recipes")
    public List<RecipeDto> getRecipesFor(@PathVariable Long id) {
        ProductDto product = productService.findById(id, currentUserProvider.getCurrentUserId());
        return recipeService.forCategory(product.categoryName());
    }
}
