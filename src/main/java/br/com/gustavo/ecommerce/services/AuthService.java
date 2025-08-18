package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.entities.User;
import br.com.gustavo.ecommerce.services.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    public void validateSelfOrAdmin(Long userId) {
        User me = userService.authenticated();
        //if (!me.hasRole("ROLE_ADMIN") && !me.getId().equals(userId)) {
          //  throw new ForbiddenException("Acesso negado");
        //}
        if (me.hasRole("ROLE_ADMIN")) {
            return;
        }
        if (!me.getId().equals(userId)) {
            throw new ForbiddenException("Acesso negado. Deve ser próprio ou admin.");
        }
    }
}
