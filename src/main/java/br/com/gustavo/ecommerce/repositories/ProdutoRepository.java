package br.com.gustavo.ecommerce.repositories;

import br.com.gustavo.ecommerce.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
