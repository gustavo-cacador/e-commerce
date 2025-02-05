package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.ProdutoDTO;
import br.com.gustavo.ecommerce.entities.Produto;
import br.com.gustavo.ecommerce.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    /*
    @Transactional(readOnly = true)
    public ProdutoDTO findById(Long id) {
        Optional<Produto> result = produtoRepository.findById(id);
        Produto produto = result.get();
        ProdutoDTO dto = new ProdutoDTO(produto);
        return dto;
    }
     */

    @Transactional(readOnly = true)
    public ProdutoDTO findById(Long id) {
        Produto produto = produtoRepository.findById(id).get();
        return new ProdutoDTO(produto);
    }
}
