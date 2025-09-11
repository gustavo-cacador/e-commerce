package br.com.gustavo.ecommerce.controllers;

import br.com.gustavo.ecommerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class ProductControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private Long existingProductId, nonExistingProductId, dependentProductId;
    private String productName;

    private Map<String, Object> postProductInstance;
    private Map<String, Object> putProductInstance;

    @BeforeEach
    void setUp() {
        baseURI = "http://localhost:8080";

        clientUsername = "alex@gmail.com";
        clientPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto";

        productName = "Macbook";

        postProductInstance = new HashMap<>();
        postProductInstance.put("name", "Iphone 15");
        postProductInstance.put("description", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim");
        postProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
        postProductInstance.put("price", 40.0);

        putProductInstance = new HashMap<>();
        putProductInstance.put("name", "Produto atualizado");
        putProductInstance.put("description", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim");
        putProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
        putProductInstance.put("price", 200.0);

        List<Map<String, Object>> categories = new ArrayList<>();

        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", 2);

        Map<String, Object> category2 = new HashMap<>();
        category2.put("id", 3);

        categories.add(category1);
        categories.add(category2);

        postProductInstance.put("categories", categories);
        putProductInstance.put("categories", categories);
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() {

        existingProductId = 2L;

        given()
                .get("products/{id}", existingProductId)
                .then()
                .statusCode(200)
                .body("id", is(2))
                .body("name", equalTo("Smart TV"))
                .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
                .body("price", is(2190.0F))
                .body("categories.id", hasItems(2,3))
                .body("categories.name", hasItems("Eletrônicos", "Computadores"));
    }

    // busca por id retorna NotFound qnd id do produto n existe
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {

        nonExistingProductId = 100L;

        given()
                .get("/movies/{id}", nonExistingProductId)
                .then()
                .statusCode(404)
                .body("error", equalTo("Not Found"))
                .body("status", equalTo(404));
    }

    // busca paginada existe listagem paginada qnd campo nome nao preenchido e checa se os produtos Macbook Pro e PC Gamer Tera estao contidos
    @Test
    public void findAllShouldReturnPageProductsWhenProductNameIsEmpty() {

        given()
                .get("products?page=0")
                .then()
                .statusCode(200)
                .body("content.name", hasItems("Macbook Pro", "PC Gamer Tera"));
    }

    // busca paginada filtra produtos por nome e exibe listagem paginada qnd campo nome preenchido
    @Test
    public void findAllShouldReturnPageProductsWhenProductNameIsNotEmpty() {

        given()
                .get("products?name={productName}", productName)
                .then()
                .statusCode(200)
                .body("content.id[0]", is(3))
                .body("content.name[0]", equalTo("Macbook Pro"))
                .body("content.price[0]", is(1250.0F))
                .body("content.imgUrl[0]", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
    }

    // busca paginada filtra produtos de forma paginada e filtra produtos com preço maior que 2000
    @Test
    public void findAllShouldReturnPagedProductsWithPriceGreaterThen2000() {

        given()
                .get("products?size=25")
                .then()
                .statusCode(200)
                .body("content.findAll { it.price > 2000 }.name", hasItems("Smart TV", "PC Gamer Hera"));
    }

    // insercao de produto insere produto com dados validos e qnd admin estiver logado
    @Test
    public void insertShouldReturnProductCreatedWhenAdminLogged() {

        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("products")
                .then()
                .statusCode(201)
                .body("name", equalTo("Iphone 15"))
                .body("description", equalTo("Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim"))
                .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
                .body("price", is(40.0F))
                .body("categories.id", hasItems(2, 3));
    }

    // insercao de produto retorna 422 e mensagens customizadas com dados invalidos qnd logado como admin e campo name for invalido
    @Test
    public void insertShouldReturnUnprocessbleEntityWhenAdminLoggedAndInvalidName() {

        postProductInstance.put("name", "ab");
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Nome precisa ter de 3 a 80 caracteres"));
    }

    // insercao de produto retorna 422 e mensagens customizadas com dados invalidos qnd logado como admin e campo description for invalido
    @Test
    public void insertShouldReturnUnprocessbleEntityWhenAdminLoggedAndInvalidDescription() {

        postProductInstance.put("description", "ab");
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Descrição precisa ter no mínimo 10 caracteres"));
    }

    // insercao de produto retorna 422 e mensagens customizadas com dados invalidos qnd logado como admin e campo price for negativo
    @Test
    public void insertShouldReturnUnprocessbleEntityWhenAdminLoggedAndPriceIsNegative() {

        postProductInstance.put("price", -50.0);
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("O preço precisa ser positivo"));
    }

    // insercao de produto retorna 422 e mensagens customizadas com dados invalidos qnd logado como admin e campo price for 0
    @Test
    public void insertShouldReturnUnprocessbleEntityWhenAdminLoggedAndPriceIsZero() {

        postProductInstance.put("price", 0.0);
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("O preço precisa ser positivo"));
    }

    // insercao de produto retorna 422 e mensagens customizadas com dados invalidos qnd logado como admin e qnd n tiver categoria associada
    @Test
    public void insertShouldReturnUnprocessbleEntityWhenAdminLoggedAndProductHasNoCategory() {

        postProductInstance.put("categories", null);
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Deve ter pelo menos uma categoria"));
    }

    // outra forma de fazer
    @Test
    public void insertShouldReturnUnprocessbleEntityWhenAdminLoggedAndProductHasNoCategory2() {

        postProductInstance.put("categories", null);
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("products")
                .then()
                .statusCode(422)
                .body("errors.fieldNome", hasItems("categories"))
                .body("errors.message", hasItems("Deve ter pelo menos uma categoria"));
    }

    // insercao de produto retorna 403 qnd logado como cliente
    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() {

        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("products")
                .then()
                .statusCode(403);
    }

    // insercao de produto retorna 401 qnd nao logado como cliente nem admin
    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() {

        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("products")
                .then()
                .statusCode(401);
    }

    // atualizacao de produto retorna 200 ok ao atualizar produto qnd id do produto existe e estiver logado como admin
    @Test
    public void updateShouldReturnProductWhenIdExistsAndAdminLogged() {

        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 10L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Produto atualizado"))
                .body("price", is(200.0f))
                .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
                .body("categories.id", hasItems(2, 3))
                .body("categories.name", hasItems("Eletrônicos", "Computadores"));
    }

    // atualizacao deve retornar 404 qnd id do produto n existe e estiver logado como admin
    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() {

        JSONObject product = new JSONObject(putProductInstance);
        nonExistingProductId = 100L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", nonExistingProductId)
                .then()
                .statusCode(404)
                .body("error", equalTo("Produto com id: " + nonExistingProductId + ", não encontrado."))
                .body("status", equalTo(404));
    }

    // atualizacao deve retornar UnprocessableEntity qnd id do produto existe, admin estiver logado, porém nome for inválido
    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndInvalidName() {

        putProductInstance.put("name", "ab");
        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 10L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(product)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422);
    }

    // atualizacao deve retornar UnprocessableEntity qnd id do produto existe, admin estiver logado, porém descricao for inválida
    @Test
    public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidDescription() {

        putProductInstance.put("description", "ab");
        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 10L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(product)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422);
    }

    // atualizacao deve retornar UnprocessableEntity qnd id do produto existe, admin estiver logado, porém preco for negativo
    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndPriceIsNegative() {

        putProductInstance.put("price", -2.0);
        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 10L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(product)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422);
    }

    // atualizacao deve retornar UnprocessableEntity qnd id do produto existe, admin estiver logado, porém preco for 0
    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndPriceIsZero() {

        putProductInstance.put("price", 0.0);
        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 10L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(product)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422);
    }

    // atualizacao deve retornar UnprocessableEntity qnd id do produto existe, admin estiver logado, porém produto n tiver nenhuma categoria associada
    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndProductHasNoCategory() {

        putProductInstance.put("categories", null);
        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 10L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(product)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log()
                .all()
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422);
    }

    // atualizacao deve retornar forbidden 403 qnd id do produto existe mas estiver logado como cliente
    @Test
    public void updateShouldReturnForbiddenWhenIdExistsAndClientLogged() {

        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 10L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(403);
    }

    // atualizacao deve retornar 401 n autorizado qnd id do produto existe mas n estiver logado como admin
    @Test
    public void updateShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() {

        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 10L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(401);
    }

    // delecao de produto deleta produto existente qnd logado como admin
    @Test
    public void deleteShouldReturnNoContentWhenIdExistsAndAdminLogged() {

        existingProductId = 25L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("products/{id}", existingProductId)
                .then()
                .statusCode(204);
    }

    // delecao de produto retorna 404 para produto inexistente qnd logado como admin
    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() {

        nonExistingProductId = 100L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("products/{id}", nonExistingProductId)
                .then()
                .statusCode(404)
                .body("error", equalTo("Produto com id: " + nonExistingProductId + ", não encontrado."))
                .body("status", equalTo(404));
    }

    // delecao de produto retorna 400 para produto dependente (produto inserido num pedido por exemplo) qnd logado como admin
    @Test
    public void deleteShouldReturnBadRequestWhenDependentIdAndAdminLogged() {

        dependentProductId = 3L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("products/{id}", dependentProductId)
                .then()
                .statusCode(400);
    }

    // delecao de produto retorna 403 qnd logado como cliente
    @Test
    public void deleteShouldReturnForbiddenWhenClientLogged() {

        existingProductId = 4L;

        given()
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .delete("products/{id}", existingProductId)
                .then()
                .statusCode(403);
    }

    // delecao de produto retorna 401 qnd n logado como cliente nem admin
    @Test
    public void deleteShouldReturnUnauthorizedWhenInvalidToken() {

        existingProductId = 4L;

        given()
                .header("Authorization", "Bearer " + invalidToken)
                .when()
                .delete("products/{id}", existingProductId)
                .then()
                .statusCode(401);
    }
}
