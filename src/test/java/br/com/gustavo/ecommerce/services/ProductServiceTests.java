package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.dto.ProductDTO;
import br.com.gustavo.ecommerce.entities.Product;
import br.com.gustavo.ecommerce.repositories.ProductRepository;
import br.com.gustavo.ecommerce.services.exceptions.ResourceNotFoundException;
import br.com.gustavo.ecommerce.tests.ProductFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private long existingProductId, nonExistingProductId;
    private String productName;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {

        existingProductId = 1L;
        nonExistingProductId = 2L;
        productName = "Play5";

        product = ProductFactory.createProduct(productName);

        Mockito.when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());
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
}
