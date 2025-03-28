package br.com.gustavo.ecommerce.repositories;

import br.com.gustavo.ecommerce.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
