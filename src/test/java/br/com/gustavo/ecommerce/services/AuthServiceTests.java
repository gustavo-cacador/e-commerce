package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.entities.User;
import br.com.gustavo.ecommerce.services.exceptions.ForbiddenException;
import br.com.gustavo.ecommerce.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    private User admin, selfClient, otherClient;

    @BeforeEach
    void setUp() throws Exception {
        admin = UserFactory.createAdminUser();
        selfClient = UserFactory.createCustomClientUser(1L, "Bob");
        otherClient = UserFactory.createCustomClientUser(2L, "Ana");
    }

    // teste para validar se o usuario logado é admin
    @Test
    public void validateSelfOrAdminShouldDoNothingWhenAdminLogged() {

        Mockito.when(userService.authenticated()).thenReturn(admin);

        Long userId = admin.getId();

        Assertions.assertDoesNotThrow(() -> {
            authService.validateSelfOrAdmin(userId);
        });
    }

    // teste para validar se o usuario é o próprio usuario (para ele ser autenticado e autorizado a ver os pedidos dele)
    @Test
    public void validateSelfOrAdminShouldDoNothingWhenSelfLogged() {

        Mockito.when(userService.authenticated()).thenReturn(selfClient);

        Long userId = selfClient.getId();

        Assertions.assertDoesNotThrow(() -> {
            authService.validateSelfOrAdmin(userId);
        });
    }

    // teste para retornar ForbiddenException caso o usuario logado n é admin nem ele mesmo para ver o pedido
    // exemplo: usuario ROLE_CLIENT tem apenas o pedido com id 1, mas ele está tentando acessar o pedido com id 2
    @Test
    public void validateSelfOrAdminThrowsForbiddenExceptionWhenClientOtherLogged() {

        Mockito.when(userService.authenticated()).thenReturn(selfClient);

        Long userId = otherClient.getId();

        Assertions.assertThrows(ForbiddenException.class, () -> {
            authService.validateSelfOrAdmin(userId);
        });
    }
}
