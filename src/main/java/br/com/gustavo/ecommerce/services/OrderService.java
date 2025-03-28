package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.OrderItemDTO;
import br.com.gustavo.ecommerce.dto.OrderDTO;
import br.com.gustavo.ecommerce.entities.*;
import br.com.gustavo.ecommerce.repositories.OrderItemRepository;
import br.com.gustavo.ecommerce.repositories.OrderRepository;
import br.com.gustavo.ecommerce.repositories.ProductRepository;
import br.com.gustavo.ecommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order pedido = orderRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pedido com id: " + id + ", não encontrado."));
        // testa se esse usuario eh o dono do pedido ou admin para autorizar para visualizar o pedido
        authService.validateSelfOrAdmin(pedido.getCliente().getId());
        return new OrderDTO(pedido);
    }

    @Transactional
    public OrderDTO insert(OrderDTO dto) {

        Order pedido = new Order();
        pedido.setMomento(Instant.now());
        pedido.setStatus(OrderStatus.AGUARDANDO_PAGAMENTO);

        User user = userService.authenticated();
        pedido.setCliente(user);

        for (OrderItemDTO orderItemDTO : dto.getItens()) {
            Product produto = productRepository.getReferenceById(orderItemDTO.getProdutoId());
            OrderItem item = new OrderItem(pedido, produto, orderItemDTO.getQuantity(), produto.getPrice());
            pedido.getItems().add(item);
        }

        orderRepository.save(pedido);
        orderItemRepository.saveAll(pedido.getItems());

        return new OrderDTO(pedido);
    }
}
