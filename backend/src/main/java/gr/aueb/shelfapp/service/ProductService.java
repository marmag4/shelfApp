package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.dto.CreateProductRequest;
import gr.aueb.shelfapp.dto.ProductDto;
import gr.aueb.shelfapp.entity.Category;
import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import gr.aueb.shelfapp.entity.User;
import gr.aueb.shelfapp.repository.CategoryRepository;
import gr.aueb.shelfapp.repository.ProductRepository;
import gr.aueb.shelfapp.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository,
                           CategoryRepository categoryRepository,
                           UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public ProductDto create(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Product product = new Product(
                request.name(), request.quantity(), request.unit(), request.expiryDate(), category, user);

        return toDto(productRepository.save(product));
    }

    public List<ProductDto> findByUser(Long userId) {
        return productRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    public ProductDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    public ProductDto updateStatus(Long id, String newStatus) {
        Product product = getOrThrow(id);

        ProductStatus status;
        try {
            status = ProductStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "status must be one of ACTIVE, CONSUMED, DONATED, WASTED");
        }

        product.setStatus(status);
        return toDto(productRepository.save(product));
    }

    private Product getOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private ProductDto toDto(Product p) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getQuantity(),
                p.getUnit(),
                p.getExpiryDate(),
                p.getStatus().name(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getUser().getId()
        );
    }
}
