package br.com.gustavo.ecommerce.repositories;

import br.com.gustavo.ecommerce.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
