package br.com.gustavo.ecommerce.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.gustavo.ecommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class UserControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String adminToken, clientToken, invalidToken;

    @BeforeEach
    public void setup() throws JSONException {
        baseURI = "http://localhost:8080";

        clientUsername = "alex@gmail.com";
        clientPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto";
    }

    // buscar usuario logado retorna usuario qnd cliente estiver logado
    @Test
    public void getMeShouldReturnUserWhenClientLogged() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("id", is(2))
                .body("nome", equalTo("Alex Green"))
                .body("email", equalTo("alex@gmail.com"))
                .body("telefone", equalTo("977777777"))
                .body("dataNascimento", equalTo("1987-12-13"))
                .body("roles", hasItems("ROLE_CLIENT"));
    }

    // buscar usuario logado retorna usuario qnd admin estiver logado
    @Test
    public void getMeShouldReturnUserWhenAdminLogged() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("nome", equalTo("Maria Brown"))
                .body("email", equalTo("maria@gmail.com"))
                .body("telefone", equalTo("988888888"))
                .body("dataNascimento", equalTo("2001-07-25"))
                .body("roles", hasItems("ROLE_CLIENT", "ROLE_ADMIN"));
    }

    // buscar usuario logado retorna 401 qnd n for um admin nem cliente
    @Test
    public void getMeShouldReturnUnauthorizedWhenInvalidToken() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(ContentType.JSON)
                .when()
                .get("/users/me")
                .then()
                .statusCode(401);
    }
}
