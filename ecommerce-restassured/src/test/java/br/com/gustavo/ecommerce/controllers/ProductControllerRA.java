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
    private Long existingProductId, nonExistingProductId;
    private String productName;

    private Map<String, Object> postProductInstance;

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

        List<Map<String, Object>> categories = new ArrayList<>();

        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", 2);

        Map<String, Object> category2 = new HashMap<>();
        category2.put("id", 3);

        categories.add(category1);
        categories.add(category2);

        postProductInstance.put("categories", categories);
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
}
