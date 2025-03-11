package br.com.gustavo.ecommerce.repositories;

import br.com.gustavo.ecommerce.entities.ItemPedido;
import br.com.gustavo.ecommerce.entities.ItemPedidoPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, ItemPedidoPK> {
}
