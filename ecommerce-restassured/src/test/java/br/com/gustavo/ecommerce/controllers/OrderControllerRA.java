package br.com.gustavo.ecommerce.controllers;

import br.com.gustavo.ecommerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class OrderControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword, adminOnlyUsername, adminOnlyPassword;
    private String clientToken, adminToken, adminOnlyToken, invalidToken;
    private Long existingOrdertId, nonExistingOrderId;

    private Map<String, List<Map<String, Object>>> postOrderInstance;

    @BeforeEach
    void setUp() {
        baseURI = "http://localhost:8080";

        existingOrdertId = 1L;
        nonExistingOrderId = 100L;

        clientUsername = "alex@gmail.com";
        clientPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";
        adminOnlyUsername = "ana@gmail.com";
        adminOnlyPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        adminOnlyToken = TokenUtil.obtainAccessToken(adminOnlyUsername, adminOnlyPassword);
        invalidToken = adminToken + "xpto";

        Map<String, Object> item1 = new HashMap<>();
        item1.put("produtoId", 1);
        item1.put("quantity", 2);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("produtoId", 5);
        item2.put("quantity", 1);

        List<Map<String,Object>> itemInstances = new ArrayList<>();
        itemInstances.add(item1);
        itemInstances.add(item2);

        postOrderInstance = new HashMap<>();
        postOrderInstance.put("itens", itemInstances);
    }

    // busca de pedido por id retorna pedido existente qnd logado como admin
    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged() {

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("orders/{id}", existingOrdertId)
                .then()
                .statusCode(200)
                // verificacao do response
                .body("id", is(1))
                .body("momento", equalTo("2022-07-25T13:00:00Z"))
                .body("status", equalTo("PAGO"))
                .body("cliente.nome", equalTo("Maria Brown"))
                .body("pagamento.momento", equalTo("2022-07-25T15:00:00Z"))
                .body("itens.nome", hasItems("The Lord of the Rings", "Macbook Pro"))
                .body("total", is(1431.0F));
    }

    // busca de pedido por id retorna pedido existente qnd logado como cliente e o pedido pertence ao usuario
    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClientLogged() {

        Long otherOrderId = 2L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("orders/{id}", otherOrderId)
                .then()
                .statusCode(200)
                // verificacao do response
                .body("id", is(2))
                .body("momento", equalTo("2022-07-29T15:50:00Z"))
                .body("status", equalTo("ENTREGUE"))
                .body("cliente.nome", equalTo("Alex Green"))
                .body("pagamento.momento", equalTo("2022-07-30T11:00:00Z"))
                .body("itens.nome", hasItem("Macbook Pro"))
                .body("total", is(1250.0F));
    }

    // busca de pedido retorna 403 qnd o id do pedido existe e qnd o pedido n pertence ao usuario
    @Test
    public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongUser() {

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("orders/{id}", existingOrdertId)
                .then()
                .statusCode(403);
    }

    // busca de pedido por id retorna 404 para pedido inexistente qnd logado como admin
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() {

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("orders/{id}", nonExistingOrderId)
                .then()
                .statusCode(404);
    }

    // busca de pedido por id retorna 404 para pedido inexistente qnd logado como cliente
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndClientLogged() {

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("orders/{id}", nonExistingOrderId)
                .then()
                .statusCode(404);
    }

    // busca de pedido por ir retorna 401 qnd n logado como admin nem cliente
    @Test
    public void findByIdShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() {

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .when()
                .get("orders/{id}", existingOrdertId)
                .then()
                .statusCode(401);
    }

    // insercao de pedido retorna 201 com dados validos qnd cliente logado
    @Test
    public void insertShouldReturnOrderCreatedWhenClientLogged() {

        JSONObject newOrder = new JSONObject(postOrderInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newOrder)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .body("status", equalTo("AGUARDANDO_PAGAMENTO"))
                .body("cliente.nome", equalTo("Alex Green"))
                .body("itens.nome", hasItems("The Lord of the Rings", "Rails for Dummies"))
                .body("total", is(281.99F));
    }

    // insercao de pedido retorna 422 qnd cliente logado e pedido n tem item nenhum
    @Test
    public void insertShouldReturnUnprocessableEntityWhenClientLoggedAndOrderHasNoItem() {

        postOrderInstance.put("itens", null);
        JSONObject newOrder = new JSONObject(postOrderInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newOrder)
                .when()
                .post("/orders")
                .then()
                .statusCode(422);
    }

    // insercao de pedido retorna 403 qnd admin estiver logado
    @Test
    public void insertShouldReturnForbiddenWhenAdminLogged() {

        JSONObject newOrder = new JSONObject(postOrderInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminOnlyToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newOrder)
                .when()
                .post("/orders")
                .then()
                .statusCode(403);
    }

    // insercao de pedido retorna 401 qnd n for um cliente logado
    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() {

        JSONObject newOrder = new JSONObject(postOrderInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newOrder)
                .when()
                .post("/orders")
                .then()
                .statusCode(401);
    }
}
