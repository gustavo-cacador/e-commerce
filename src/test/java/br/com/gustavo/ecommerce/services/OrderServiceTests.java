package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.OrderDTO;
import br.com.gustavo.ecommerce.entities.Order;
import br.com.gustavo.ecommerce.entities.User;
import br.com.gustavo.ecommerce.repositories.OrderRepository;
import br.com.gustavo.ecommerce.tests.OrderFactory;
import br.com.gustavo.ecommerce.tests.UserFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AuthService authService;

    private Long existingOrderId, nonExistingId;
    private Order order;
    private OrderDTO orderDTO;
    private User admin, client;

    void setUp() throws Exception {
        existingOrderId = 1L;
        nonExistingId = 2L;

        admin = UserFactory.createCustomAdminUser(1L, "Jef");
        client = UserFactory.createCustomClientUser(2L, "Bob");

        order = OrderFactory.createOrder(client);

        orderDTO = new OrderDTO(order);

        Mockito.when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.findById(nonExistingId)).thenReturn(Optional.empty());
    }
}
