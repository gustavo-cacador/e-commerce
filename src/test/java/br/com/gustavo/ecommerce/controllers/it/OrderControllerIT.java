package br.com.gustavo.ecommerce.controllers.it;

import br.com.gustavo.ecommerce.dto.OrderDTO;
import br.com.gustavo.ecommerce.entities.*;
import br.com.gustavo.ecommerce.tests.ProductFactory;
import br.com.gustavo.ecommerce.tests.TokenUtil;
import br.com.gustavo.ecommerce.tests.UserFactory;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private Long existingOrderId, otherOrderId, nonExistingOrderId;

    private Order order;
    private OrderDTO orderDTO;
    private User user;

    @BeforeEach
    void setUp() throws Exception {

        clientUsername = "alex@gmail.com";
        clientPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";

        existingOrderId = 1L;
        otherOrderId = 2L;
        nonExistingOrderId = 100L;

        adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
        invalidToken = adminToken + "xpto"; // simulando senha errada

        user = UserFactory.createClientUser();
        order = new Order(null, Instant.now(), OrderStatus.AGUARDANDO_PAGAMENTO, user, null);

        Product product = ProductFactory.createProduct();
        OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
        order.getItems().add(orderItem);
    }

    // busca de pedido por ir retorna pedido existente qnd logado como admin
    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() throws Exception {

        ResultActions result = mockMvc
                .perform(get("/orders/{id}", existingOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingOrderId));
        result.andExpect(jsonPath("$.momento").value("2022-07-25T13:00:00Z"));
        result.andExpect(jsonPath("$.status").value("PAGO"));
        result.andExpect(jsonPath("$.cliente").exists());
        result.andExpect(jsonPath("$.cliente.nome").value("Maria Brown"));
        result.andExpect(jsonPath("$.pagamento").exists());
        result.andExpect(jsonPath("$.itens").exists());
        result.andExpect(jsonPath("$.itens[1].nome").value("Macbook Pro"));
        result.andExpect(jsonPath("$.total").exists());
    }

    // busca de pedido por id retorna pedido existente qnd logado como cliente e o pedido pertence ao usuario
    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndClientLogged() throws Exception {

        ResultActions result = mockMvc
                .perform(get("/orders/{id}", otherOrderId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(2L));
        result.andExpect(jsonPath("$.momento").value("2022-07-29T15:50:00Z"));
        result.andExpect(jsonPath("$.status").value("ENTREGUE"));
        result.andExpect(jsonPath("$.cliente").exists());
        result.andExpect(jsonPath("$.cliente.nome").value("Alex Green"));
        result.andExpect(jsonPath("$.pagamento").exists());
        result.andExpect(jsonPath("$.itens").exists());
        result.andExpect(jsonPath("$.itens[0].nome").value("Macbook Pro"));
        result.andExpect(jsonPath("$.total").exists());
    }

    // busca de pedido por id retorna 403 qnd pedido n pertence ao usuario (logado com perfil de cliente)
    @Test
    public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongUser() throws Exception {

        ResultActions result = mockMvc
                .perform(get("/orders/{id}", existingOrderId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isForbidden());
    }

    // busca de pedido por id nao existente retorna 404 para pedido inexistente qnd logado como admin
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {

        ResultActions result = mockMvc
                .perform(get("/orders/{id}", nonExistingOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isNotFound());
    }

    // busca de pedido por id nao existente retorna 404 para pedido inexistente qnd logado como cliente
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndClientLogged() throws Exception {

        ResultActions result = mockMvc
                .perform(get("/orders/{id}", nonExistingOrderId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isNotFound());
    }

    // busca de pedido por id retorna 401 qnd nao logado como admin ou cliente
    @Test
    public void findByIdShouldReturnUnauthorizedWhenIdExistsAndIdNotExistAndInvalidToken() throws Exception {

        ResultActions result = mockMvc
                .perform(get("/orders/{id}", existingOrderId, nonExistingOrderId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isUnauthorized());
    }
}
