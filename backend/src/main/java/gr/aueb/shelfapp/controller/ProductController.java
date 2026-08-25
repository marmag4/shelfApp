package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.CreateProductRequest;
import gr.aueb.shelfapp.dto.ProductDto;
import gr.aueb.shelfapp.dto.UpdateProductStatusRequest;
import gr.aueb.shelfapp.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * NOTE: for now we pass userId explicitly (as a request field / query param)
 * since we haven't wired up login yet. Once auth exists, this will read the
 * current user from the security context instead - see the roadmap.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }

    @GetMapping
    public List<ProductDto> getByUser(@RequestParam Long userId) {
        return productService.findByUser(userId);
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PatchMapping("/{id}/status")
    public ProductDto updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateProductStatusRequest request) {
        return productService.updateStatus(id, request.status());
    }
}
