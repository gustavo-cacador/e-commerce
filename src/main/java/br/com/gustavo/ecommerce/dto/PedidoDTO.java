package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.ItemPedido;
import br.com.gustavo.ecommerce.entities.Pedido;
import br.com.gustavo.ecommerce.entities.PedidoStatus;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PedidoDTO {

    private Long id;
    private Instant momento;
    private PedidoStatus status;

    private ClienteDTO cliente;

    private PagamentoDTO pagamento;

    // criando a relação para mostrar as categorias dos produtos
    @NotEmpty(message = "Deve ter pelo menos um item")
    private List<ItemPedidoDTO> itens = new ArrayList<>();

    public PedidoDTO() {
    }

    public PedidoDTO(Long id, Instant momento, PedidoStatus status, ClienteDTO cliente, PagamentoDTO pagamento) {
        this.id = id;
        this.momento = momento;
        this.status = status;
        this.cliente = cliente;
        this.pagamento = pagamento;
    }

    public PedidoDTO(Pedido entity) {
        this.id = entity.getId();
        this.momento = entity.getMomento();
        this.status = entity.getStatus();
        this.cliente = new ClienteDTO(entity.getCliente());
        this.pagamento = (entity.getPagamento() == null) ? null : new PagamentoDTO(entity.getPagamento());

        // para cada ItemPedido, vou pegar o "getItems" da entidade Pedido, e vou instanciar um ItemPedidoDTO, para assim adicionar itens no meu itemDto (ItemPedidoDTO)
        for (ItemPedido item : entity.getItems()) {
            ItemPedidoDTO itemDto = new ItemPedidoDTO(item);
            itens.add(itemDto);
        }
    }

    public Long getId() {
        return id;
    }

    public Instant getMomento() {
        return momento;
    }

    public PedidoStatus getStatus() {
        return status;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public PagamentoDTO getPagamento() {
        return pagamento;
    }

    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    public Double getTotal() {
        double sum = 0.0;
        for (ItemPedidoDTO item : itens) {
            //sum = sum + item.getSubTotal();
            sum += item.getSubTotal();
        }
        return sum;
    }
}
