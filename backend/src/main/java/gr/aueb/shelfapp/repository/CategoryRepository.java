package gr.aueb.shelfapp.repository;

import gr.aueb.shelfapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The Repository layer: talks to the database, nothing else.
 * Spring Data JPA writes the implementation at startup.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
