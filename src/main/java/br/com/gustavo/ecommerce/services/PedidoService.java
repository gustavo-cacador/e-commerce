package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.PedidoDTO;
import br.com.gustavo.ecommerce.dto.ProdutoDTO;
import br.com.gustavo.ecommerce.entities.Pedido;
import br.com.gustavo.ecommerce.entities.Produto;
import br.com.gustavo.ecommerce.repositories.PedidoRepository;
import br.com.gustavo.ecommerce.repositories.ProdutoRepository;
import br.com.gustavo.ecommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public PedidoDTO findById(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pedido com id: " + id + ", não encontrado."));
        return new PedidoDTO(pedido);
    }
}
