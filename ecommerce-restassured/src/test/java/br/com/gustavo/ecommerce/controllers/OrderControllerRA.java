package br.com.gustavo.ecommerce.controllers;

import br.com.gustavo.ecommerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class OrderControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private Long existingOrdertId, nonExistingOrderId;

    @BeforeEach
    void setUp() {
        baseURI = "http://localhost:8080";

        existingOrdertId = 1L;
        nonExistingOrderId = 100L;

        clientUsername = "alex@gmail.com";
        clientPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto";
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
}
