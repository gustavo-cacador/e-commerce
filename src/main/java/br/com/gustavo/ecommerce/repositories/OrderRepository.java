package br.com.gustavo.ecommerce.repositories;

import br.com.gustavo.ecommerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
