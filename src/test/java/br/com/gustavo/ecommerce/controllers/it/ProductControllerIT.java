package br.com.gustavo.ecommerce.controllers.it;

import br.com.gustavo.ecommerce.dto.ProductDTO;
import br.com.gustavo.ecommerce.entities.Category;
import br.com.gustavo.ecommerce.entities.Product;
import br.com.gustavo.ecommerce.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingProductId, nonExistingProductId, dependentProductId;
    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;

    private String productName;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {

        clientUsername = "alex@gmail.com";
        clientPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";

        productName = "Macbook";

        existingProductId = 2L;
        nonExistingProductId = 100L;
        dependentProductId = 3L;

        adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
        invalidToken = adminToken + "xpto"; // simulando senha errada

        Category category = new Category(2L, "Eletrônico");
        product = new Product(null, "PlayStation 5", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim", 2190.0, "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
        product.getCategories().add(category); // associando categoria ao produto que criamos
        productDTO = new ProductDTO(product);

    }

    @Test
    public void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception{

        ResultActions result = mockMvc
                .perform(get("/products?name={productName}", productName)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].id").value(3L));
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[0].price").value(1250.0));
        result.andExpect(jsonPath("$.content[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
    }

    @Test
    public void findAllShouldReturnPageWhenNameParamIsEmpty() throws Exception{

        ResultActions result = mockMvc
                .perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].id").value(1L));
        result.andExpect(jsonPath("$.content[0].name").value("The Lord of the Rings"));
        result.andExpect(jsonPath("$.content[0].price").value(90.5));
        result.andExpect(jsonPath("$.content[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
    }

    // busca de produto por id retorna 200 qnd id do produto existe
    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/products/{id}", existingProductId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(2L));
        result.andExpect(jsonPath("$.name").value("Smart TV"));
        result.andExpect(jsonPath("$.description").value("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        result.andExpect(jsonPath("$.price").value(2190.0));
        result.andExpect(jsonPath("$.categories").exists());
    }

    // busca de produto por id retorna 404 qnd id do pedido nao existe
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/products/{id}", nonExistingProductId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    // insercao de produto insere produto com dados válidos quando logado como admin
    @Test
    public void insertShouldReturnProductDTOCreatedWhenAdminLogged() throws Exception{

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").value(27L));
        result.andExpect(jsonPath("$.name").value("PlayStation 5"));
        result.andExpect(jsonPath("$.description").value("Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim"));
        result.andExpect(jsonPath("$.price").value(2190.0));
        result.andExpect(jsonPath("$.imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
        result.andExpect(jsonPath("$.categories[0].id").value(2L));
        //result.andExpect(jsonPath("$.categories[0].name").value("Eletrônico"));
    }

    // insercao de produto retorna UnprocessableEntity qnd admin logado e nome do produto invalido
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception {

        product.setName("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    // insercao de produto retorna UnprocessableEntity qnd admin logado e qnd for descricao invalida
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidDescription() throws Exception {

        product.setDescription("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    // insercao de produto retorna UnprocessableEntity qnd admin logado e qnd preço for negativo
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsNegative() throws Exception {

        product.setPrice(-2.0);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    // insercao de produto retorna UnprocessableEntity qnd admin logado e qnd preço for 0
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsZero() throws Exception {

        product.setPrice(0.0);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    // insercao de produto retorna UnprocessableEntity qnd admin logado e qnd produto nao tiver categoria
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndProductHasNotCategory() throws Exception {

        product.getCategories().clear();
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    // insercao de produto retorna Forbidden qnd cliente logado
    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + clientToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    // insercao de produto retorna Unauthorized qnd for um token invalido (nao for cliente nem admin)
    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + invalidToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    // atualizacao do produto retorna 200 qnd id do produto existe e qnd admin estiver logado
    @Test
    public void updateShouldReturnProductDTOWhenIdExistsAndAdminLogged() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(2L));
        result.andExpect(jsonPath("$.name").value("PlayStation 5"));
        result.andExpect(jsonPath("$.description").value("Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim"));
        result.andExpect(jsonPath("$.price").value(2190.0));
        result.andExpect(jsonPath("$.imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
        result.andExpect(jsonPath("$.categories[0].id").value(2L));
    }

    // atualizacao do produto retorna 404 qnd id do pedido nao existe e admin estiver logado
    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    // atualizacao do produto retorna 422 qnd id do produto existe, qnd admin estiver logado e nome do produto for invalido
    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndInvalidName() throws Exception {

        product.setName("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    // atualizacao do produto retorna 422 qnd id do produto existe, qnd admin estiver logado e descricao do produto for invalida
    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndInvalidDescription() throws Exception {

        product.setDescription("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    // atualizacao do produto retorna 422 qnd id do produto existe, qnd admin estiver logado e preco do produto for negativo
    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndPriceIsNegative() throws Exception {

        product.setPrice(-2.0);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    // atualizacao do produto retorna 422 qnd id do produto existe, qnd admin estiver logado e preco do produto for 0
    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndPriceIsZero() throws Exception {

        product.setPrice(0.0);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    // atualizacao do produto retorna 422 qnd id do produto existe, qnd admin estiver logado e produto n tiver nenhuma categoria associada
    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndProductHasNoCategory() throws Exception {

        product.getCategories().clear();
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    // atualizacao do produto retorna 403 qnd id do produto existe mas qnd cliente estiver logado
    @Test
    public void updateShouldReturnForbiddenWhenIdExistsAndClientLogged() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + clientToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    // atualizacao do produto retorna 401 qnd id do produto existe mas qnd for um token invalido (admin n estiver logado)
    @Test
    public void updateShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    // delecao de produto retorna no content qnd for um produto existente e qnd for um admin logado
    @Test
    public void deleteShouldReturnNoContentWhenIdExistsAndAdminLogged() throws Exception {

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    // delecao de produto retorna 404 (notfound) qnd for um produto inexistente e qnd for um admin logado
    @Test
    public void deleteShouldReturnNotFoundWhenIdNotExistAndAdminLogged() throws Exception {

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", nonExistingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    // delecao de produto retorna 400 (badrequest) qnd for um produto dependente (qnd um produto estiver inserido num pedido) e qnd for um admin logado
    @Test
    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteShouldReturnBadRequestWhenDependentProductIdAndAdminLogged() throws Exception {

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", dependentProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());
    }

    // delecao de produto retorna 403 (forbidden) qnd for um produto existente ou inexistente qnd um cliente estiver logado
    @Test
    public void deleteShouldReturnForbiddenWhenIdExistsAndIdNotExistAndClientLogged() throws Exception {

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", existingProductId, nonExistingProductId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    // delecao de produto retorna 401 (unauthorized) qnd for um produto existente e qnd for um token invalido (n for admin nem cliente)
    @Test
    public void deleteShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() throws Exception {

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }
}
