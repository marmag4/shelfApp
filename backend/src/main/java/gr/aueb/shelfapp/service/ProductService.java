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

    public ProductDto create(CreateProductRequest request, Long userId) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        User user = userRepository.findById(userId)
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

    public ProductDto findById(Long id, Long currentUserId) {
        return toDto(getOwnedOrThrow(id, currentUserId));
    }

    /** Edits a product's own details (name/quantity/unit/expiry/category) - not its status. */
    public ProductDto update(Long id, CreateProductRequest request, Long currentUserId) {
        Product product = getOwnedOrThrow(id, currentUserId);

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        product.setName(request.name());
        product.setQuantity(request.quantity());
        product.setUnit(request.unit());
        product.setExpiryDate(request.expiryDate());
        product.setCategory(category);

        return toDto(productRepository.save(product));
    }

    public ProductDto updateStatus(Long id, String newStatus, Long currentUserId) {
        Product product = getOwnedOrThrow(id, currentUserId);

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

    /** Loads the product and makes sure it actually belongs to the logged-in user. */
    private Product getOwnedOrThrow(Long id, Long currentUserId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!product.getUser().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This product does not belong to you");
        }

        return product;
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
