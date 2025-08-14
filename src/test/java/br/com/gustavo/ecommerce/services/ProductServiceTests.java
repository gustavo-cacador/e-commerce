package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.ProductDTO;
import br.com.gustavo.ecommerce.dto.ProductMinDTO;
import br.com.gustavo.ecommerce.entities.Product;
import br.com.gustavo.ecommerce.repositories.ProductRepository;
import br.com.gustavo.ecommerce.services.exceptions.DatabaseException;
import br.com.gustavo.ecommerce.services.exceptions.ResourceNotFoundException;
import br.com.gustavo.ecommerce.tests.ProductFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private long existingProductId, nonExistingProductId, dependentProductId;
    private String productName;
    private Product product;
    private ProductDTO productDTO;
    private PageImpl<Product> page;

    @BeforeEach
    void setUp() throws Exception {

        existingProductId = 1L;
        nonExistingProductId = 2L;
        dependentProductId = 3L;
        productName = "Play5";
        product = ProductFactory.createProduct(productName);
        productDTO = new ProductDTO(product);
        page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.searchByName(any(), (Pageable)any())).thenReturn(page);

        Mockito.when(productRepository.save(any())).thenReturn(product);

        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(productRepository.existsById(existingProductId)).thenReturn(true);
        Mockito.when(productRepository.existsById(dependentProductId)).thenReturn(true);
        Mockito.when(productRepository.existsById(nonExistingProductId)).thenReturn(false);

        Mockito.doNothing().when(productRepository).deleteById(existingProductId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentProductId);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {

        ProductDTO result = productService.findById(existingProductId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingProductId);
        Assertions.assertEquals(result.getName(), product.getName());
    }

    @Test
    public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingProductId);
        });
    }

    @Test
    public void findAllShouldReturnPagedProductMinDTO() {

        Pageable pageable = PageRequest.of(0, 12);

        Page<ProductMinDTO> result = productService.findAll(productName, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getSize(), 1);
        Assertions.assertEquals(result.iterator().next().getName(), productName);
    }

    @Test
    public void insertShouldReturnProductDTO() {

        ProductDTO result = productService.insert(productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), product.getId());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {

        ProductDTO result = productService.update(existingProductId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingProductId);
        Assertions.assertEquals(result.getName(), productDTO.getName());
    }

    @Test
    public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(nonExistingProductId, productDTO);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            productService.delete(existingProductId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingProductId);
        });
    }

    // só vai excluir o produto caso o produto n seja dependente de nenhuma outra entidade (exemplo: produto está vinculado a um pedido, ou a um usuario)
    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentEntityExists() {

        Assertions.assertThrows(DatabaseException.class, () -> {
            productService.delete(dependentProductId);
        });
    }
}
