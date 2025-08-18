package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.OrderDTO;
import br.com.gustavo.ecommerce.entities.Order;
import br.com.gustavo.ecommerce.entities.OrderItem;
import br.com.gustavo.ecommerce.entities.Product;
import br.com.gustavo.ecommerce.entities.User;
import br.com.gustavo.ecommerce.repositories.OrderItemRepository;
import br.com.gustavo.ecommerce.repositories.OrderRepository;
import br.com.gustavo.ecommerce.repositories.ProductRepository;
import br.com.gustavo.ecommerce.services.exceptions.ForbiddenException;
import br.com.gustavo.ecommerce.services.exceptions.ResourceNotFoundException;
import br.com.gustavo.ecommerce.tests.OrderFactory;
import br.com.gustavo.ecommerce.tests.ProductFactory;
import br.com.gustavo.ecommerce.tests.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AuthService authService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserService userService;

    private Long existingOrderId, nonExistingId;
    private Long existingProductId, nonExistingProductId;
    private Order order;
    private OrderDTO orderDTO;
    private User admin, client;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        existingOrderId = 1L;
        nonExistingId = 2L;

        existingProductId = 1L;
        nonExistingProductId = 2L;

        admin = UserFactory.createCustomAdminUser(1L, "Jef");
        client = UserFactory.createCustomClientUser(2L, "Bob");

        order = OrderFactory.createOrder(client);

        orderDTO = new OrderDTO(order);

        product = ProductFactory.createProduct();

        Mockito.when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(orderRepository.save(any())).thenReturn(order);

        Mockito.when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>(order.getItems()));
    }

    // teste para retornar OrderDTO qnd id do pedido existir e qnd for admin logado
    // tentando acessar um pedido existente com perfil de admin
    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() {

        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

        OrderDTO result = orderService.findById(existingOrderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingOrderId);
    }

    // tentando acessar um pedido existente com perfil de cliente
    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndSelfClientLogged() {

        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

        OrderDTO result = orderService.findById(existingOrderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingOrderId);
    }

    // teste para retornar ForbiddenException qnd cliente n estiver autorizado, cliente logado tentando acessar pedido de outro cliente
    @Test
    public void findByIdShouldThrowsForbiddenExceptionWhenIdExistsAndOtherClientLogged() {

        Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ForbiddenException.class, () -> {
            @SuppressWarnings("unused")
            OrderDTO result = orderService.findById(existingOrderId);
        });
    }

    // teste para retornar ResourceNotFoundException quando id do pedido nao existir
    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            @SuppressWarnings("unused")
            OrderDTO result = orderService.findById(nonExistingId);
        });
    }

    @Test
    public void insertShouldReturnOrderDTOWhenAdminLogged() {

        Mockito.when(userService.authenticated()).thenReturn(admin);

        OrderDTO result = orderService.insert(orderDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    public void insertShouldReturnOrderDTOWhenClientLogged() {

        Mockito.when(userService.authenticated()).thenReturn(client);

        OrderDTO result = orderService.insert(orderDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    public void insertShouldThrowsUsernameNotFoundExceptionWhenUserNotLogged() {

        Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();

        order.setCliente(new User());
        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            @SuppressWarnings("unused")
            OrderDTO result = orderService.insert(orderDTO);
        });
    }

    // teste para retornar EntityNotFoundException quando o id do produto nao existir
    @Test
    public void insertShouldThrowsEntityNotFoundExceptionWhenOrderProductIdDoesNotExist() {

        Mockito.when(userService.authenticated()).thenReturn(client);

        product.setId(nonExistingProductId);
        OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
        order.getItems().add(orderItem);

        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            @SuppressWarnings("unused")
            OrderDTO result = orderService.insert(orderDTO);
        });
    }
}
