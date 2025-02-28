package br.com.gustavo.ecommerce.services;

import br.com.gustavo.ecommerce.entities.Role;
import br.com.gustavo.ecommerce.entities.Usuario;
import br.com.gustavo.ecommerce.projections.UsuarioDetailsProjection;
import br.com.gustavo.ecommerce.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UsuarioDetailsProjection> result  = usuarioRepository.searchUserAndRolesByEmail(username);
        if (result.size() == 0) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(username);
        usuario.setSenha(result.get(0).getSenha());
        for (UsuarioDetailsProjection projection : result) {
            usuario.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }

        return usuario;
    }
}
