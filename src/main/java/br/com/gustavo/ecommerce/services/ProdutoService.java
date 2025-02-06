package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.ProdutoDTO;
import br.com.gustavo.ecommerce.entities.Produto;
import br.com.gustavo.ecommerce.repositories.ProdutoRepository;
import br.com.gustavo.ecommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public ProdutoDTO findById(Long id) {
        Produto produto = produtoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Produto com id: " + id + ", não encontrado."));
        return new ProdutoDTO(produto);
    }

    // Para buscar todos os produtos da lista
    /*
    @Transactional(readOnly = true)
    public List<ProdutoDTO> buscarTodos() {
        List<Produto> result = produtoRepository.findAll();
        return result
                .stream()
                .map(x -> new ProdutoDTO(x))
                .toList();
    }
     */

    @Transactional(readOnly = true)
    public Page<ProdutoDTO> findAll(Pageable pageable) {
        Page<Produto> result = produtoRepository.findAll(pageable);
        return result.map(x -> new ProdutoDTO(x));
    }

    @Transactional
    public ProdutoDTO insert(ProdutoDTO dto) {
        Produto entity = new Produto();
        copyDtoToEntity(dto, entity);
        entity = produtoRepository.save(entity);
        return new ProdutoDTO(entity);
    }

    @Transactional
    public ProdutoDTO update(Long id, ProdutoDTO dto) {
        try {
            Produto entity = produtoRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = produtoRepository.save(entity);
            return new ProdutoDTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Produto com id: " + id + ", não encontrado.");
        }
    }

    @Transactional
    public void delete(Long id) {
        produtoRepository.deleteById(id);
    }

    private void copyDtoToEntity(ProdutoDTO dto, Produto entity) {
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setPreco(dto.getPreco());
        entity.setImgUrl(dto.getImgUrl());
    }

}
