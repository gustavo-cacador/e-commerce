package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.CategoryDTO;
import br.com.gustavo.ecommerce.dto.ProductDTO;
import br.com.gustavo.ecommerce.dto.ProductMinDTO;
import br.com.gustavo.ecommerce.entities.Category;
import br.com.gustavo.ecommerce.entities.Product;
import br.com.gustavo.ecommerce.repositories.ProductRepository;
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
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product produto = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Produto com id: " + id + ", não encontrado."));
        return new ProductDTO(produto);
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
    public Page<ProductMinDTO> findAll(String name, Pageable pageable) {
        Page<Product> result = productRepository.searchByName(name, pageable);
        return result.map(x -> new ProductMinDTO(x));
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
        entity = productRepository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = productRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = productRepository.save(entity);
            return new ProductDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Produto com id: " + id + ", não encontrado.");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!productRepository.existsById(id)){
            throw new ResourceNotFoundException("Produto com id: " + id + ", não encontrado.");
        } try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());

        // limpamos as categorias relacionadas aos produtos e depois atualizamos as categorias
        entity.getCategories().clear();
        for (CategoryDTO categoryDTO : dto.getCategories()) {
            Category categoria = new Category();
            categoria.setId(categoryDTO.getId());
            entity.getCategories().add(categoria);
        }
    }
}
