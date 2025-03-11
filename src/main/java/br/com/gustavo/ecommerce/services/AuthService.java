package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.entities.Usuario;
import br.com.gustavo.ecommerce.services.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioService usuarioService;

    public void validateSelfOrAdmin(long userId) {
        Usuario me = usuarioService.authenticated();
        if (!me.hasRole("ROLE_ADMIN") && !me.getId().equals(userId)) {
            throw new ForbiddenException("Acesso negado");
        }
    }
}
