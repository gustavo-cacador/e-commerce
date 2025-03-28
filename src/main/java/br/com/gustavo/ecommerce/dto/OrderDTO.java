package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.OrderItem;
import br.com.gustavo.ecommerce.entities.Order;
import br.com.gustavo.ecommerce.entities.OrderStatus;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

    private Long id;
    private Instant momento;
    private OrderStatus status;

    private ClienteDTO cliente;

    private PaymentDTO pagamento;

    // criando a relação para mostrar as categorias dos produtos
    @NotEmpty(message = "Deve ter pelo menos um item")
    private List<OrderItemDTO> itens = new ArrayList<>();

    public OrderDTO() {
    }

    public OrderDTO(Long id, Instant momento, OrderStatus status, ClienteDTO cliente, PaymentDTO pagamento) {
        this.id = id;
        this.momento = momento;
        this.status = status;
        this.cliente = cliente;
        this.pagamento = pagamento;
    }

    public OrderDTO(Order entity) {
        this.id = entity.getId();
        this.momento = entity.getMomento();
        this.status = entity.getStatus();
        this.cliente = new ClienteDTO(entity.getCliente());
        this.pagamento = (entity.getPagamento() == null) ? null : new PaymentDTO(entity.getPagamento());

        // para cada ItemPedido, vou pegar o "getItems" da entidade Pedido, e vou instanciar um ItemPedidoDTO, para assim adicionar itens no meu itemDto (ItemPedidoDTO)
        for (OrderItem item : entity.getItems()) {
            OrderItemDTO itemDto = new OrderItemDTO(item);
            itens.add(itemDto);
        }
    }

    public Long getId() {
        return id;
    }

    public Instant getMomento() {
        return momento;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public PaymentDTO getPagamento() {
        return pagamento;
    }

    public List<OrderItemDTO> getItens() {
        return itens;
    }

    public Double getTotal() {
        double sum = 0.0;
        for (OrderItemDTO item : itens) {
            //sum = sum + item.getSubTotal();
            sum += item.getSubTotal();
        }
        return sum;
    }
}
