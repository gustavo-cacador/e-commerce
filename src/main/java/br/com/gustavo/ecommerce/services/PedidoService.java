package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.ItemPedidoDTO;
import br.com.gustavo.ecommerce.dto.PedidoDTO;
import br.com.gustavo.ecommerce.entities.*;
import br.com.gustavo.ecommerce.repositories.ItemPedidoRepository;
import br.com.gustavo.ecommerce.repositories.PedidoRepository;
import br.com.gustavo.ecommerce.repositories.ProdutoRepository;
import br.com.gustavo.ecommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public PedidoDTO findById(Long id) {
        Order pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pedido com id: " + id + ", não encontrado."));
        // testa se esse usuario eh o dono do pedido ou admin para autorizar para visualizar o pedido
        authService.validateSelfOrAdmin(pedido.getCliente().getId());
        return new PedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO insert(PedidoDTO dto) {

        Order pedido = new Order();
        pedido.setMomento(Instant.now());
        pedido.setStatus(PedidoStatus.AGUARDANDO_PAGAMENTO);

        Usuario usuario = usuarioService.authenticated();
        pedido.setCliente(usuario);

        for (ItemPedidoDTO itemPedidoDTO : dto.getItens()) {
            Product produto = produtoRepository.getReferenceById(itemPedidoDTO.getProdutoId());
            ItemPedido item = new ItemPedido(pedido, produto, itemPedidoDTO.getQuantidade(), produto.getPrice());
            pedido.getItems().add(item);
        }

        pedidoRepository.save(pedido);
        itemPedidoRepository.saveAll(pedido.getItems());

        return new PedidoDTO(pedido);
    }
}
