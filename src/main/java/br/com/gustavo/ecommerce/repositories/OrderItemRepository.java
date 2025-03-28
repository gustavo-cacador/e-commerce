package br.com.gustavo.ecommerce.repositories;

import br.com.gustavo.ecommerce.entities.OrderItem;
import br.com.gustavo.ecommerce.entities.ItemPedidoPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, ItemPedidoPK> {
}
