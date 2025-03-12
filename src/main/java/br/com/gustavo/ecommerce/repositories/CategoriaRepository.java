package br.com.gustavo.ecommerce.repositories;

import br.com.gustavo.ecommerce.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
