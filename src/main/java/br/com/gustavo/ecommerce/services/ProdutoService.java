package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.CategoriaDTO;
import br.com.gustavo.ecommerce.dto.ProdutoDTO;
import br.com.gustavo.ecommerce.dto.ProdutoMinDTO;
import br.com.gustavo.ecommerce.entities.Categoria;
import br.com.gustavo.ecommerce.entities.Product;
import br.com.gustavo.ecommerce.repositories.ProdutoRepository;
import br.com.gustavo.ecommerce.services.exceptions.DatabaseException;
import br.com.gustavo.ecommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public ProdutoDTO findById(Long id) {
        Product produto = produtoRepository.findById(id).orElseThrow(
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

    @Transactional(readOnly = true)
    public Page<ProdutoDTO> findAll(String nome, Pageable pageable) {
        Page<Produto> result = produtoRepository.searchByName(nome, pageable);
        return result.map(x -> new ProdutoDTO(x));
    }
     */

    @Transactional(readOnly = true)
    public Page<ProdutoMinDTO> findAll(String name, Pageable pageable) {
        Page<Product> result = produtoRepository.searchByName(name, pageable);
        return result.map(x -> new ProdutoMinDTO(x));
    }

    @Transactional
    public ProdutoDTO insert(ProdutoDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
        entity = produtoRepository.save(entity);
        return new ProdutoDTO(entity);
    }

    @Transactional
    public ProdutoDTO update(Long id, ProdutoDTO dto) {
        try {
            Product entity = produtoRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = produtoRepository.save(entity);
            return new ProdutoDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Produto com id: " + id + ", não encontrado.");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!produtoRepository.existsById(id)){
            throw new ResourceNotFoundException("Produto com id: " + id + ", não encontrado.");
        } try {
            produtoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
        produtoRepository.deleteById(id);
    }

    private void copyDtoToEntity(ProdutoDTO dto, Product entity) {
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setPreco(dto.getPreco());
        entity.setImgUrl(dto.getImgUrl());

        // limpamos as categorias relacionadas aos produtos e depois atualizamos as categorias
        entity.getCategorias().clear();
        for (CategoriaDTO categoriaDTO : dto.getCategorias()) {
            Categoria categoria = new Categoria();
            categoria.setId(categoriaDTO.getId());
            entity.getCategorias().add(categoria);
        }
    }
}
