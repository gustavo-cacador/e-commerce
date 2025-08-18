package br.com.gustavo.ecommerce.tests;

import br.com.gustavo.ecommerce.entities.*;

import java.time.Instant;

public class OrderFactory {

    public static Order createOrder(User client) {

        Order order = new Order(1L, Instant.now(), OrderStatus.AGUARDANDO_PAGAMENTO, client, new Payment());

        Product product = ProductFactory.createProduct();
        OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
        order.getItems().add(orderItem);
        return order;
    }
}
